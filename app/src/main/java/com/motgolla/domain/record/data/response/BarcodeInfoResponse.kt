package com.motgolla.domain.record.data.response

import com.google.gson.annotations.SerializedName

data class BarcodeInfoResponse(

    @SerializedName("brand")
    val brand : String,

    @SerializedName("product_id")
    val productId : Long,

    @SerializedName("product_name")
    val productName: String,

    @SerializedName("product_number")
    val productNumber: String
)
