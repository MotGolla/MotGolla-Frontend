package com.motgolla.domain.login.api.service

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.motgolla.common.storage.TokenStorage
import com.motgolla.domain.login.data.TokenResponse
import com.motgolla.common.RetrofitClient
import com.motgolla.domain.login.data.SignUpRequest

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

    fun signUpWithInput(
        context: Context,
        navController: NavController,
        gender: String,
        year: Int,
        month: Int,
        day: Int
    ) {
        val birthday = "${year}-${month.pad2()}-${day.pad2()}"

        val signUpRequest = com.motgolla.domain.login.data.SignUpRequest(
            idToken = TokenStorage.getValue(context, "idToken") ?: "",
            oauthId = TokenStorage.getValue(context, "oauthId") ?: "",
            name = TokenStorage.getValue(context, "nickname") ?: "",
            profile = TokenStorage.getValue(context, "profile") ?: "",
            gender = when (gender) {
                "MALE" -> "남자"
                "FEMALE" -> "여자"
                else -> gender
            },
            birthday = birthday
        )

        Log.d("SignUp", "회원 가입 요청: $signUpRequest")
        requestSignUp(
            signUpRequest = signUpRequest,
            onSuccess = { accessToken, refreshToken ->
                Log.d("SignUp", "회원 가입 성공: $accessToken, $refreshToken")
                TokenStorage.save(context, TokenResponse(accessToken, refreshToken))
                CoroutineScope(Dispatchers.Main).launch {
                    navController.navigate("welcome") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            },
            onFailure = { throwable ->
                Log.e("SignUp", "회원 가입 실패: ${throwable.message}")
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "회원가입 실패: ${throwable.message}", Toast.LENGTH_SHORT).show()
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            }
        )
    }
}

// month 또는 day가 1자리 수일 때 앞에 0을 붙여주는 확장 함수
private fun Int.pad2(): String = this.toString().padStart(2, '0')