package com.motgolla.domain.record.data.request

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

data class RecordRequest(

    @SerializedName("tagImg")
    val tagImg: MultipartBody.Part?,

    @SerializedName("productImgs")
    val productImgs: List<MultipartBody.Part>,

    @SerializedName("departmentStoreBrandId")
    val departmentStoreBrandId: String,

    @SerializedName("productId")
    val productId: Long,

    @SerializedName("productSize")
    val productSize: String,

    @SerializedName("noteSummary")
    val noteSummary: String
)
