package com.motgolla.domain.record.data.response

import com.google.gson.annotations.SerializedName

data class BarcodeInfoResponse(

    @SerializedName("brand")
    val brand : String,

    @SerializedName("productId")
    val productId : Long,

    @SerializedName("productName")
    val productName: String,

    @SerializedName("productNumber")
    val productNumber: String
)
