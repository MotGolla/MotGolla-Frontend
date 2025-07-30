package com.motgolla.ui.screen.record

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motgolla.common.RetrofitClient
import com.motgolla.domain.record.data.request.UpdateRecordStatusRequest
import com.motgolla.domain.record.data.response.RecordDetailResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

open class RecordDetailViewModel : ViewModel() {

    private val recordCache = mutableMapOf<Long, RecordDetailResponse>()

    private val recordService = RetrofitClient.getRecordService()

    private val _uiState = MutableStateFlow(RecordDetailUiState())
    val uiState: StateFlow<RecordDetailUiState> = _uiState.asStateFlow()

    fun loadRecord(recordId: Long) {
        Log.d("RecordDetailViewModel", "recordId: " + recordId)
        if (recordCache.containsKey(recordId)) {
            _uiState.value = RecordDetailUiState(record = recordCache[recordId])
            return
        }

        viewModelScope.launch {
            try {
                val response = recordService.getRecordById(recordId)
                recordCache[recordId] = response
                _uiState.value = RecordDetailUiState(record = response)
            } catch (e: HttpException) {
                val msg = if (e.code() == 404) "등록된 기록 정보를 찾을 수 없습니다." else "네트워크 오류가 발생했습니다."
                _uiState.value = RecordDetailUiState(errorMessage = msg)
            } catch (e: Exception) {
                _uiState.value = RecordDetailUiState(errorMessage = "알 수 없는 오류가 발생했습니다.")
            }
        }
    }

    fun changeStatus(recordId: Long, newStatus: String) {
        viewModelScope.launch {
            try {
                val request = UpdateRecordStatusRequest(status = newStatus)
                val response = recordService.updateRecordState(recordId, request)
                if (response.isSuccessful) {
                    // 캐시 무효화
                    recordCache.remove(recordId)
                    loadRecord(recordId)
                } else {
                    _uiState.update {
                        it.copy(errorMessage = "상태 변경 실패 (${response.code()}): ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "상태 변경 오류: ${e.message ?: "예기치 못한 오류"}")
                }
            }
        }
    }
}