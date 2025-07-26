package com.motgolla.domain.login.api.service

import android.util.Log
import com.motgolla.common.RetrofitClient
import com.motgolla.domain.login.data.SignUpRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SignUpService {
    fun requestSignUp(
        signUpRequest: SignUpRequest,
        onSuccess: (accessToken: String, refreshToken: String) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.getAuthService().signUp(signUpRequest)
                if (response.isSuccessful) {
                    Log.d("SignUp", "회원가입 성공: ${response.body()}")
                    val tokenResponse = response.body()
                    if (tokenResponse != null) {
                        onSuccess(tokenResponse.accessToken, tokenResponse.refreshToken)
                    } else {
                        onFailure(Throwable("서버 응답은 성공했지만 데이터가 없음"))
                    }
                } else {
                    onFailure(Throwable("회원가입 실패: ${response.code()}"))
                }
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }
}