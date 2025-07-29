package com.motgolla.domain.record.data.request

import com.google.gson.annotations.SerializedName

data class UpdateRecordStatusRequest(

    @SerializedName("status")
    val status: String,
)
