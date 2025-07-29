package com.motgolla.viewmodel.vote

import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motgolla.domain.vote.api.VoteRepository
import com.motgolla.domain.vote.data.request.VoteCreateRequest
import com.motgolla.ui.screen.vote.ProductItem
import kotlinx.coroutines.launch

class VoteCreateViewModel(
    private val repository: VoteRepository = VoteRepository() // 수동 DI
) : ViewModel() {

    val selectedRecordIds = mutableStateListOf<Long>()
    var title = mutableStateOf("")

    var products = mutableStateListOf<ProductItem>()
    var isLoading by mutableStateOf(false)
    var hasNext by mutableStateOf(true)
    private var cursor: Long? = null

    val canProceed = derivedStateOf { selectedRecordIds.size in 2..4 }

    private val _selectedProducts = mutableStateListOf<ProductItem>()
    val selectedProducts: List<ProductItem> get() = _selectedProducts

    fun isSelected(product: ProductItem): Boolean {
        return _selectedProducts.contains(product)
    }

    fun canProceedToNext(): Boolean {
        return selectedRecordIds.size in 2..4
    }

    fun loadProducts(date: String) {
        if (isLoading || !hasNext) return
        isLoading = true

        viewModelScope.launch {
            try {
                val response = repository.getProducts(date, "전체", cursor, 10)
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        Log.d("VoteProduct", "응답 성공: ${data.items.size}개, nextCursor=${data.nextCursor}, hasNext=${data.hasNext}")
                        data.items.forEach {
                            Log.d("VoteProduct", "상품: id=${it.record_id}, name=${it.product_name}, brand=${it.brand_name}")
                        }

                        products.addAll(data.items)
                        cursor = data.nextCursor
                        hasNext = data.hasNext
                    }
                } else {
                    Log.w("VoteProduct", "응답 실패: code=${response.code()}, message=${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("VoteProduct", "네트워크 에러", e)

            } finally {
                isLoading = false
            }
        }
    }

    fun toggleRecordId(id: Long) {
        if (selectedRecordIds.contains(id)) {
            selectedRecordIds.remove(id)
        } else {
            if (selectedRecordIds.size < 4) {
                selectedRecordIds.add(id)
            }
        }
        Log.d("VoteVM", "현재 선택된 ID: ${selectedRecordIds.joinToString()}")
    }

    fun setTitle(value: String) {
        title.value = value
    }

    fun submitVote(onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        val request = VoteCreateRequest(
            recordIds = selectedRecordIds.toList(),
            title = title.value
        )
        Log.d("VoteSubmit", "생성된 request: $request")

        viewModelScope.launch {
            try {
                val response = repository.createVote(request)
                if (response.isSuccessful) {
                    Log.d("VoteSubmit", "title: ${title.value}, recordIds: $selectedRecordIds")
                    onSuccess()
                    reset()
                } else {
                    onError(Throwable("투표 등록 실패: ${response.code()}"))
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun reset() {
        selectedRecordIds.clear()
        title.value = ""
    }
}
