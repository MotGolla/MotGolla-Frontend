package com.motgolla.common.storage

import android.content.Context
import android.util.Log
import com.motgolla.domain.login.data.TokenResponse

object TokenStorage {
    private const val PREF_NAME = "auth_pref"
    private const val KEY_ACCESS = "accessToken"
    private const val KEY_REFRESH = "refreshToken"

    fun save(context: Context, tokenResponse: TokenResponse) {
        Log.d("TokenStorage", "Token save $tokenResponse")
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_ACCESS, tokenResponse.accessToken)
            .putString(KEY_REFRESH, tokenResponse.refreshToken)
            .apply()
    }

    fun getTokenResponse(context: Context): TokenResponse? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val accessToken = prefs.getString(KEY_ACCESS, null)
        val refreshToken = prefs.getString(KEY_REFRESH, null)
        return if (accessToken != null && refreshToken != null) {
            Log.d("Get Token", "$accessToken, $refreshToken")
            TokenResponse(accessToken, refreshToken)
        } else null
    }

    fun clear(context: Context) {
        Log.d("TokenStorage", "Token clear")
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}