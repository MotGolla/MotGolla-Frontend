package com.motgolla.common.data

data class ErrorResponse(
    val status: Int,
    val errorCode: String,
    val message: String
)