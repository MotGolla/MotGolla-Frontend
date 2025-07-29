package com.motgolla.ui.screen.vote

data class ProductItem(
    val record_id: Long,
    val state: String,
    val img_url: String?,
    val product_name: String,
    val brand_name: String,
    val brand_floor: String,
    val price: String
)