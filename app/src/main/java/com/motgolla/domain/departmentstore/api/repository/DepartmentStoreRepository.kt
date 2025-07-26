package com.motgolla.domain.departmentstore.api.repository

import com.motgolla.BuildConfig
import com.motgolla.domain.departmentstore.api.service.DepartmentStoreService
import com.motgolla.domain.departmentstore.data.DepartmentStoreResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DepartmentStoreRepository {

    private val token = "..토큰"  // 임시 고정 토큰(이후 프론트 로그인 적용 후 수정할 예정)

    // 인터셉터로 모든 요청에 Authorization 헤더 추가
    private val authInterceptor = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            return chain.proceed(newRequest)
        }
    }

    private val api: DepartmentStoreService by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)  // OkHttpClient 설정 추가
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DepartmentStoreService::class.java)
    }

    suspend fun fetchNearestStore(lat: Double, lon: Double): DepartmentStoreResponse? {
        val response = api.getNearestStore(lat, lon)
        return if (response.isSuccessful) response.body() else null
    }
}