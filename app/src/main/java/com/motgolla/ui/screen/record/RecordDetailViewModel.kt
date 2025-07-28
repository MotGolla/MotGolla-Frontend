package com.motgolla.ui.screen.record


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motgolla.common.RetrofitClient
import com.motgolla.domain.record.data.response.RecordDetailResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RecordDetailViewModel : ViewModel() {

    private val recordCache = mutableMapOf<Long, RecordDetailResponse>()

    private val recordService = RetrofitClient.getRecordService()

    private val _uiState = MutableStateFlow(RecordDetailUiState())
    val uiState: StateFlow<RecordDetailUiState> = _uiState.asStateFlow()

    fun loadRecord(recordId: Long) {
        if (recordCache.containsKey(recordId)) {
            _uiState.value = RecordDetailUiState(record = recordCache[recordId])
            return
        }

        viewModelScope.launch {
            try {
                val response = recordService.getRecordById(recordId)
                _uiState.value = RecordDetailUiState(record = response)
            } catch (e: HttpException) {
                val msg = if (e.code() == 404) "등록된 기록 정보를 찾을 수 없습니다." else "네트워크 오류가 발생했습니다."
                _uiState.value = RecordDetailUiState(errorMessage = msg)
            } catch (e: Exception) {
                _uiState.value = RecordDetailUiState(errorMessage = "알 수 없는 오류가 발생했습니다.")
            }
        }
    }
}