package com.motgolla.domain.vote.data.response

import com.motgolla.ui.screen.vote.ProductItem

data class ProductResponse(
    val items: List<ProductItem>,
    val nextCursor: Long?,
    val hasNext: Boolean
)