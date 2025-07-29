package com.motgolla.common.storage

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.motgolla.domain.login.data.TokenResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.RequestBody.Companion.toRequestBody

class TokenInterceptor(
    private val context: Context,
    private val client: OkHttpClient
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val tokenResponse = TokenStorage.getTokenResponse(context)

        val requestWithAccess = tokenResponse?.accessToken?.let {
            original.newBuilder()
                .addHeader("Authorization", "Bearer ${tokenResponse.accessToken}")
                .build()
        } ?: original

        val response = chain.proceed(requestWithAccess)
        val peekedBody = response.peekBody(Long.MAX_VALUE)
        Log.d("SignUp", "응답: ${peekedBody.string()}")

        // 만료된 토큰인 경우 재발급 요청
        if (response.code == 401 && tokenResponse?.refreshToken != null) {
            val reissueRequest = okhttp3.Request.Builder()
                .url("http://10.0.2.2:8080/api/member/reissue")
                .addHeader("Authorization-Refresh", "Bearer ${tokenResponse.refreshToken}")
                .post(ByteArray(0).toRequestBody(null))
                .build()

            Log.d("TokenInterceptor", "재발급 요청: $reissueRequest")
            val reissueResponse = client.newCall(reissueRequest).execute()
            // 재발급 성공한 경우
            if (reissueResponse.isSuccessful) {
                Log.d("TokenInterceptor", "액세스 토큰 재발급 성공")
                val gson = com.google.gson.Gson()
                val newToken = gson.fromJson(reissueResponse.body?.charStream(), TokenResponse::class.java)
                // 토큰 저장
                val mergedToken = TokenResponse(
                    accessToken = newToken.accessToken,
                    refreshToken = tokenResponse.refreshToken
                )
                TokenStorage.save(context, mergedToken)
                // 재요청
                val newRequest = original.newBuilder()
                    .addHeader("Authorization", "Bearer ${newToken.accessToken}")
                    .build()

                return chain.proceed(newRequest)
            } else {
                if (response.code == 500) {
                    Toast.makeText(context, "서버 에러!", Toast.LENGTH_SHORT).show()
                }
                Log.e("TokenInterceptor", "토큰 만료. 로그 아웃")
                TokenStorage.clear(context)
                TokenManager.tokenErrorFlow.tryEmit(Unit)
            }
        }

        return response
    }
}