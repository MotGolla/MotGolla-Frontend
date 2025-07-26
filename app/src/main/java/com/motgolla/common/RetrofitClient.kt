package com.motgolla.common

import com.google.gson.GsonBuilder
import com.motgolla.BuildConfig

import com.motgolla.common.storage.TokenInterceptor
import com.motgolla.domain.login.api.service.AuthService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = BuildConfig.BASE_URL

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
    }

    private var authService: AuthService? = null

    fun init(context: android.content.Context, navController: androidx.navigation.NavController) {
        val client = okhttp3.OkHttpClient.Builder().build()
        val tokenInterceptor = TokenInterceptor(context, client)
        val finalClient = client.newBuilder()
            .addInterceptor(tokenInterceptor)
            .build()

        authService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(finalClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
            .create(AuthService::class.java)
    }

    fun getAuthService(): AuthService {
        return authService ?: throw IllegalStateException("RetrofitClient is not initialized. Call init(context, navController) first.")
    }
}