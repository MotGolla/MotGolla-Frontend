package com.motgolla.domain.example.data

import com.google.gson.annotations.SerializedName

data class ExampleResponse (
    @SerializedName("receiptId")
    val receiptId: Int,

    @SerializedName("storeId")
    val storeId: Int,

    @SerializedName("totalPrice")
    val totalPrice: Int,

    @SerializedName("status")
    val status: String,

    @SerializedName("createdDate")
    val createdDate: String
)
