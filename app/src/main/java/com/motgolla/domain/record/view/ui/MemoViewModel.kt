package com.motgolla.domain.record.view.ui

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.motgolla.domain.record.api.repository.RecordRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MemoViewModel(application: Application) : AndroidViewModel(application) {
    private val recordRepository = RecordRepository()

    // 해당 메모를 openAI를 통해 요약 --> 백엔드 단에서 진행
    private val _memo = MutableStateFlow("")
    val memo: StateFlow<String> = _memo
    fun setMemo(new: String) {
        _memo.value = new
    }

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _recordingTime = MutableStateFlow("00:00")
    val recordingTime: StateFlow<String> = _recordingTime

    private var startTimeMillis: Long = 0L
    private var recordingJob: Job? = null

    private var speechRecognizer: SpeechRecognizer? = null
    private var hasResult = false
    private var latestPartialResult: String? = null

    fun startRecording() {
        val context = getApplication<Application>().applicationContext
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        }

        hasResult = false
        latestPartialResult = null

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("MemoViewModel", "STT 준비 완료")
            }

            override fun onBeginningOfSpeech() {
                Log.d("MemoViewModel", "음성 입력 시작")
            }

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                Log.d("MemoViewModel", "음성 입력 종료")
                // 여기서는 stopRecording을 호출하지 않음, 결과 기다림
            }

            override fun onError(error: Int) {
                Log.e("MemoViewModel", "STT 에러 발생: $error")
                stopRecording()
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches.joinToString(" ")
                    Log.d("MemoViewModel", "인식된 텍스트: $recognizedText")
                    // 서버 요약 요청
                    recordRepository.summarizeMemo(recognizedText) { result ->
                        result.onSuccess { summary ->
                            _memo.value = summary
                            Log.d("MemoViewModel", "요약 성공: $summary")
                        }.onFailure { e ->
                            _memo.value = recognizedText // 요약 실패 시 원문 저장
                            Log.e("MemoViewModel", "요약 실패: ${e.message}")
                        }
                    }
                    hasResult = true
                } else {
                    Log.w("MemoViewModel", "인식 결과가 없습니다.")
                }
                stopRecording()
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val partial =
                    partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!partial.isNullOrEmpty()) {
                    val text = partial.joinToString(" ")
                    latestPartialResult = text
//                    Log.d("MemoViewModel", "부분 인식: $text")
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer?.startListening(intent)
        Log.d("MemoViewModel", "startListening 호출됨")
        _isRecording.value = true
        startTimeMillis = System.currentTimeMillis()
        startTimer()
    }

    fun stopRecording() {
        Log.d("MemoViewModel", "stopRecording() 호출됨")

        if (!hasResult && !latestPartialResult.isNullOrBlank()) {
            _memo.value = latestPartialResult!!
            Log.d("MemoViewModel", "onResults 없이 마지막 partial 저장됨: ${latestPartialResult!!}")
        }

        try {
            speechRecognizer?.apply {
                stopListening()
                cancel()
                destroy()
            }
        } catch (e: Exception) {
            Log.e("MemoViewModel", "stopRecording 중 오류: ${e.message}")
        }
        speechRecognizer = null
        _isRecording.value = false
        stopTimer()
    }

    private fun startTimer() {
        recordingJob = viewModelScope.launch {
            while (isActive) {
                val elapsed = System.currentTimeMillis() - startTimeMillis
                val seconds = (elapsed / 1000) % 60
                val minutes = (elapsed / 1000) / 60
                _recordingTime.value = String.format("%02d:%02d", minutes, seconds)
                delay(1000)
            }
        }
    }

    private fun stopTimer() {
        recordingJob?.cancel()
        recordingJob = null
        _recordingTime.value = "00:00"
    }
}
