package com.motgolla.domain.recommend.data

data class ProductPreview(
    val id: Long,
    val name: String,
    val brand: String,
    val floor: Int,
    val price: Int,
    val imageUrl: String
)