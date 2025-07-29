package com.motgolla.domain.record.data

import com.google.gson.annotations.SerializedName

data class ShoppingRecordInfoDto(

    val state: String,

    val imgUrl: String,

    val productName: String,

    val brandName: String,

    val brandFloor: String,

    val productPrice: String
)