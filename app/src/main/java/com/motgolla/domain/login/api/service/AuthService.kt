package com.motgolla.domain.login.api.service

import com.motgolla.domain.login.data.SocialLoginRequest
import com.motgolla.domain.login.data.MemberInfoResponse
import com.motgolla.domain.login.data.NormalLoginRequest
import com.motgolla.domain.login.data.NormalSignUpRequest
import com.motgolla.domain.login.data.SocialSignUpRequest
import com.motgolla.domain.login.data.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthService {
    @POST("/api/member/login/kakao")
    suspend fun socialLogin(@Body request: SocialLoginRequest): Response<TokenResponse>

    @POST("/api/member/login")
    suspend fun login(@Body request: NormalLoginRequest): Response<TokenResponse>

    @GET("/api/member")
    suspend fun getMemberInfo(): Response<MemberInfoResponse>

    @POST("/api/member/sign-up/kakao")
    suspend fun socialSignUp(@Body request: SocialSignUpRequest): Response<TokenResponse>

    @POST("/api/member/sign-up")
    suspend fun signUp(@Body request: NormalSignUpRequest): Response<TokenResponse>

    @POST("/api/member/logout")
    suspend fun logout(): Response<Void>

    @PATCH("/api/member/resign")
    suspend fun resign(): Response<Void>

}