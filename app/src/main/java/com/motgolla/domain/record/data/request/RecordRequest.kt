package com.motgolla.domain.record.data.request

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody


import java.io.File

data class RecordRequest(

    @SerializedName("tagImg")
    val tagImg: MultipartBody.Part?,

    @SerializedName("productImgs")
    val productImgs: List<MultipartBody.Part>,

    @SerializedName("brandName")
    val brandName: String,

    // 추후 현재 위치 백화점명으로 변경 일단 임시데이터
    @SerializedName("departmentStore")
    val departmentStore: String,

    @SerializedName("productId")
    val productId: Long,

    @SerializedName("productName")
    val productName: String,

    @SerializedName("productNumber")
    val productNumber: String,

    @SerializedName("productSize")
    val productSize: String,

    @SerializedName("noteSummary")
    val noteSummary: String


)
