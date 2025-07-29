package com.motgolla.domain.record.data.response

import com.google.gson.annotations.SerializedName

data class ShoppingRecordInfoResponse(


    @SerializedName("record_id")
    val recordId: Long,

    @SerializedName("state")
    val state: String,

    @SerializedName("img_url")
    val imgUrl: String,

    @SerializedName("product_name")
    val productName: String,

    @SerializedName("brand_name")
    val brandName: String,

    @SerializedName("brand_floor")
    val brandFloor: String,

    @SerializedName("price")
    val productPrice: String,
    )
