package com.motgolla.domain.record.data.request

import com.google.gson.annotations.SerializedName

data class MemoSummaryRequest(

    @SerializedName("sttMemo")
    val sttMemo: String

)
