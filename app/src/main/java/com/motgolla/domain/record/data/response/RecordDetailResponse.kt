package com.motgolla.domain.record.data.response

data class RecordDetailResponse (
    val recordId: Long,
    val productName: String,
    val brandName: String,
    val productPrice: Int,
    val recordCreatedAt: String,
    val productSize: String,
    val productNumber: String,
    val productStatus: String,
    val productSummary: String,
    val brandLocationInfo: String,
    val imageUrls: List<String>,
    val tagImageUrl: String?,
    val storeName: String,
    val mapLink: String,
    val storeMapLink: String
)