package com.motgolla.common

import android.content.Context
import com.google.gson.GsonBuilder
import com.motgolla.BuildConfig

import com.motgolla.common.storage.TokenInterceptor
import com.motgolla.domain.login.api.service.AuthService
import com.motgolla.domain.record.api.service.RecordService
import com.motgolla.domain.vote.api.VoteService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
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
    private var recordService: RecordService? = null
    private var voteService: VoteService? = null

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


        recordService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(finalClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
            .create(RecordService::class.java)

        voteService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(finalClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
            .create(VoteService::class.java)
    }

    fun getAuthService(): AuthService {
        return authService ?: throw IllegalStateException("RetrofitClient is not initialized. Call init(context, navController) first.")
    }

    fun getRecordService() : RecordService {
        return recordService ?: throw IllegalStateException("RetrofitClient is not initialized. Call init(context, navController) first.")

    }

    fun getVoteService(): VoteService {
        return voteService ?: throw IllegalStateException("RetrofitClient is not initialized. Call init(context, navController) first.")
    }
}