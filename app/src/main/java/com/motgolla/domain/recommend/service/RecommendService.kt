package com.motgolla.domain.recommend.service

import com.motgolla.common.RetrofitClient
import com.motgolla.domain.recommend.data.ProductPreview

class RecommendService {
    suspend fun getRecommendedProducts(productId: Long): List<ProductPreview> {
        return try {
            RetrofitClient.getRecordService().getRecommendedProducts(productId)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}