package com.motgolla.domain.record.data.request

import com.google.gson.annotations.SerializedName

data class RecordProductFilterRequest(

    @SerializedName("date")
    val date: String,

    @SerializedName("category")
    val category: String?,

    @SerializedName("cursor")
    val cursor: Long?,

    @SerializedName("limit")
    val limit: Int,


)
