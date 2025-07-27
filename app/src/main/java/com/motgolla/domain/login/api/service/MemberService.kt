package com.motgolla.domain.login.api.service

import android.content.Context
import android.util.Log
import androidx.navigation.NavController
import com.motgolla.common.RetrofitClient
import com.motgolla.common.storage.TokenStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object MemberService {
    fun logout(context: Context, navController: NavController) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.getAuthService().logout()
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        TokenStorage.clear(context)
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                } else {
                    Log.e("Logout", "로그아웃 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("Logout", "에러 발생", e)
            }
        }
    }

    fun resign(context: Context, navController: NavController) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.getAuthService().resign()
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        TokenStorage.clear(context)
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                } else {
                    Log.e("Resign", "회원탈퇴 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("Resign", "에러 발생", e)
            }
        }
    }
}