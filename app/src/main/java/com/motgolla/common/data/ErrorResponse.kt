package com.example.myapplication.common.data

data class ErrorResponse(
    val status: Int,
    val errorCode: String,
    val message: String
)