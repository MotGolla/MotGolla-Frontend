package com.motgolla.domain.departmentstore.api.repository

import android.content.Context
import com.motgolla.BuildConfig
import com.motgolla.common.storage.TokenInterceptor
import com.motgolla.domain.departmentstore.api.service.DepartmentStoreService
import com.motgolla.domain.departmentstore.data.DepartmentStoreResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DepartmentStoreRepository(private val context: Context) {

    private val plainClient = OkHttpClient.Builder().build() // 재발급용
    private val tokenInterceptor = TokenInterceptor(context, plainClient)

    private val api: DepartmentStoreService by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DepartmentStoreService::class.java)
    }

    suspend fun fetchNearestStore(lat: Double, lon: Double): DepartmentStoreResponse? {
        val response = api.getNearestStore(lat, lon)
        return if (response.isSuccessful) response.body() else null
    }
}
