package com.motgolla.domain.record.data.response

import com.google.gson.annotations.SerializedName
import com.motgolla.domain.record.data.ShoppingRecordInfoDto

data class RecordProductFilterListResponse(

    @SerializedName("items")
    val items: List<ShoppingRecordInfoResponse>,

    @SerializedName("nextCursor")
    val nextCursor: Long,

    @SerializedName("hasNext")
    val hasNext: Boolean

)
