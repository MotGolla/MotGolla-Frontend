package com.motgolla.domain.record.data.response

import com.google.gson.annotations.SerializedName

data class ShoppingHistoryProduct(
    @SerializedName("record_id")
    val recordId: Long,

    @SerializedName("state")
    val state: String,

    @SerializedName("img_url")
    val imgUrl: String?,

    @SerializedName("product_name")
    val productName: String?,

    @SerializedName("brand_name")
    val brandName: String?,

    @SerializedName("brand_floor")
    val brandFloor: String?,

    @SerializedName("price")
    val price: String
)


data class ShoppingHistoryResponse(
    val items: List<ShoppingHistoryProduct>,
    val nextCursor: Long?,
    val hasNext: Boolean
)
