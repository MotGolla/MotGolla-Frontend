package com.motgolla.domain.notification.api.repository

import android.util.Log
import com.motgolla.BuildConfig
import com.motgolla.domain.notification.api.service.FcmApiService
import com.motgolla.domain.notification.data.FcmTokenRequest
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

class FcmTokenRepository {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient())
        .build()

    private val api = retrofit.create(FcmApiService::class.java)

    fun sendTokenToServer(token: String?, accessToken: String) {
        if (token == null) return

        val bearerToken = "Bearer $accessToken"
        val call = api.sendToken(FcmTokenRequest(token), bearerToken)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("FCM_TOKEN", "Token sent to server successfully")
                } else {
                    Log.w("FCM_TOKEN", "Failed to send token: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("FCM_TOKEN", "Error sending token", t)
            }
        })
    }
}