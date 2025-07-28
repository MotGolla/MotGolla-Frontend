package com.motgolla.domain.notification.api.service

import com.motgolla.domain.notification.data.FcmTokenRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST

interface FcmApiService {
    @POST("/api/notification/token")
    fun sendToken(@Body request: FcmTokenRequest,
                  @Header("Authorization") authHeader: String): Call<Void>

    @HTTP(method = "DELETE", path = "/api/notification/token", hasBody = true)
    fun deleteToken(
        @Body request: FcmTokenRequest,
        @Header("Authorization") authHeader: String
    ): Call<Void>
}