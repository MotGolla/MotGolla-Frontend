package com.motgolla.viewmodel.record

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motgolla.domain.record.api.repository.ShoppingHistoryRepository
import com.motgolla.domain.record.data.response.ShoppingHistoryProduct
import kotlinx.coroutines.launch

open class ShoppingHistoryViewModel(
    private val repository: ShoppingHistoryRepository
) : ViewModel() {

    private val _products = mutableStateListOf<ShoppingHistoryProduct>()
    val products: List<ShoppingHistoryProduct> get() = _products

    init {
        viewModelScope.launch {
            runCatching {
                repository.fetchTodayShoppingHistory()
            }.onSuccess {
                _products.clear()
                _products.addAll(it)
            }
        }
    }
}
