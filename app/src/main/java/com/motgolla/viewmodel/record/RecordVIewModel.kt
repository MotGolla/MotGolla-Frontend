package com.motgolla.viewmodel.record

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.motgolla.domain.record.api.repository.RecordRepository
import com.motgolla.domain.record.data.response.ShoppingRecordInfoResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RecordViewModel : ViewModel() {

    private val recordRepository = RecordRepository()

    private val _shoppingRecordInfoList = MutableStateFlow<List<ShoppingRecordInfoResponse>>(emptyList())
    val shoppingRecordInfoList: StateFlow<List<ShoppingRecordInfoResponse>> = _shoppingRecordInfoList

    @RequiresApi(Build.VERSION_CODES.O)
    private val _availableRecordDates = MutableStateFlow<List<LocalDate>>(emptyList())
    @RequiresApi(Build.VERSION_CODES.O)
    val availableRecordDates: StateFlow<List<LocalDate>> = _availableRecordDates

    // 페이징 관련 상태값
    private var nextCursor: Long? = null
    private var hasNext: Boolean = true

    // <--- 여기서부터 분리
    private val _isInitialLoading = MutableStateFlow(true)
    val isInitialLoading: StateFlow<Boolean> = _isInitialLoading

    private val _isPagingLoading = MutableStateFlow(false)
    val isPagingLoading: StateFlow<Boolean> = _isPagingLoading

    // 1회에 하나만 fetch 하도록 플래그(로컬)
    private var isFetching = false

    /**
     * 최초 쇼핑 기록 조회 (리스트가 비어있으면만)
     */
    fun fetchInitialShoppingRecords(category: String?, date: String) {
        if (_shoppingRecordInfoList.value.isNotEmpty()) return
        resetState()
        _isInitialLoading.value = true
        fetchMoreShoppingRecords(category, date, isInitial = true)
    }

    /**
     * 무한 스크롤 시 다음 쇼핑 기록 조회
     */
    fun fetchMoreShoppingRecords(
        category: String?,
        date: String,
        isInitial: Boolean = false
    ) {
        // 중복방지 및 끝 여부 확인
        if (isFetching || !hasNext) return

        isFetching = true
        if (isInitial) {
            // 최초 요청 때만 표시
            _isInitialLoading.value = true
        } else {
            _isPagingLoading.value = true
        }

        try {
            recordRepository.getShoppingRecords(date, category, nextCursor, limit = 10) { result ->
                result.onSuccess { response ->
                    val updatedList = _shoppingRecordInfoList.value + response.items
                    _shoppingRecordInfoList.value = mergeWithoutDuplicates(_shoppingRecordInfoList.value, updatedList)
                    nextCursor = response.nextCursor
                    hasNext = response.hasNext
                }.onFailure { e ->
                    Log.e("RecordViewModel", "쇼핑 기록 로드 실패: ${e.message}")
                }

                // 무조건 로딩 종료
                isFetching = false
                if (isInitial) {
                    _isInitialLoading.value = false
                } else {
                    _isPagingLoading.value = false
                }
            }
        } catch (e: Exception) {
            Log.e("RecordViewModel", "enqueue 자체 실패: ${e.message}")
            isFetching = false
            if (isInitial) {
                _isInitialLoading.value = false
            } else {
                _isPagingLoading.value = false
            }
        }
    }

    /**
     * 날짜나 카테고리가 변경될 때 상태 초기화 후 새로 조회
     */
    fun resetAndFetch(category: String?, date: String) {
        // 상태 리셋
        resetState()
        _isInitialLoading.value = true
        fetchMoreShoppingRecords(category, date, isInitial = true)
    }

    private fun resetState() {
        _shoppingRecordInfoList.value = emptyList()
        nextCursor = null
        hasNext = true
        isFetching = false
        // isPagingLoading 아닐 수도 있으니 명시적으로 끄기
        _isPagingLoading.value = false
    }


    // 상태 변경
    fun completeRecord(recordId: Long, category: String?, date: String) {
        recordRepository.updateRecordStateToCompleted(recordId) { result ->
            result.onSuccess {
                // 변경 성공 → 상태 초기화 후 새로 fetch
                resetAndFetch(category, date)
            }.onFailure { e ->
                Log.e("RecordViewModel", "상태 변경 실패: ${e.message}")
            }
        }
    }

    fun mergeWithoutDuplicates(
        oldList: List<ShoppingRecordInfoResponse>,
        newList: List<ShoppingRecordInfoResponse>
    ): List<ShoppingRecordInfoResponse> {
        val existingIds = oldList.map { it.recordId }.toSet()
        return oldList + newList.filter { it.recordId !in existingIds }
    }



    // 월별 요일 조회
    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchRecordDatesByYearMonth(yearMonth: String) {

        recordRepository.getRecordDates(yearMonth) { result ->
            result.onSuccess { response ->
                // String → LocalDate로 변환
                val dates = response.dates.map { LocalDate.parse(it) }
                _availableRecordDates.value = dates
            }.onFailure {
                Log.e("RecordViewModel", "쇼핑 기록 날짜 조회 실패: ${it.message}")
                _availableRecordDates.value = emptyList()
            }
        }
    }
}
