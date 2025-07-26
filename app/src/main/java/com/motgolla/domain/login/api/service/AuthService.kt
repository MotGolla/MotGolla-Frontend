package com.motgolla.domain.login.api.service

import com.motgolla.domain.login.data.LoginRequest
import com.motgolla.domain.login.data.MemberInfoResponse
import com.motgolla.domain.login.data.SignUpRequest
import com.motgolla.domain.login.data.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("/api/member/login")
    suspend fun login(@Body request: LoginRequest): Response<TokenResponse>

    @GET("/api/member")
    suspend fun getMemberInfo(): Response<MemberInfoResponse>

    @POST("/api/member/sign-up")
    suspend fun signUp(@Body request: SignUpRequest): Response<TokenResponse>

    @GET("/api/member/logout")
    suspend fun logout(): Response<Void>
}