package com.motgolla.domain.record.api.repository

import com.motgolla.domain.record.api.service.RecordService
import com.motgolla.domain.record.data.response.ShoppingHistoryProduct
import org.threeten.bp.LocalDate


open class ShoppingHistoryRepository(
    private val service: RecordService
) {
    suspend fun fetchTodayShoppingHistory(): List<ShoppingHistoryProduct> {
        val today = LocalDate.now().toString()
        return service.getShoppingHistory(today, 5).items
    }
}
