package com.motgolla.common

import android.content.Context
import com.google.gson.GsonBuilder
import com.motgolla.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = BuildConfig.BASE_URL

    // TODO: 실제 토큰 저장 위치에 맞게 수정 (예: SharedPreferences 등)
    private fun getAccessToken(): String {
        // 현재 테스트용 하드코딩. 나중에 SharedPreferences 등에서 읽어와야 함
        return ""
    }

    private val authInterceptor = Interceptor { chain ->
        val original: Request = chain.request()
        val requestBuilder = original.newBuilder()
            .header("Authorization", "Bearer ${getAccessToken()}")
        val request = requestBuilder.build()
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // 👈 인터셉터 추가된 클라이언트 사용
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
    }
}
