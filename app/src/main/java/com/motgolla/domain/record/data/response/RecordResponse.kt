package com.motgolla.domain.record.data.response

import com.google.gson.annotations.SerializedName

data class RecordResponse(

    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("extractedText")
    val extractedText: String? = null
)