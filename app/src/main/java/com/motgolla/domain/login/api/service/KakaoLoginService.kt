package com.motgolla.domain.login.api.service

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.navigation.NavController
import androidx.appcompat.app.AlertDialog
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.motgolla.common.RetrofitClient
import com.motgolla.common.storage.TokenStorage
import com.motgolla.domain.login.data.LoginRequest
import com.motgolla.domain.login.data.LoginFailedResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

object KakaoLoginService {
    fun handleKakaoLoginSuccess(
        context: Context,
        navController: NavController,
        nickname: String,
        profileImageUrl: String?,
        oauthId: String,
        idToken: String?,
    ) {
        Log.d("KakaoLogin", "카카오 로그인 성공: $nickname, $profileImageUrl, $oauthId, $idToken")
        if (idToken != null) {
            TokenStorage.save(context, "idToken", idToken)
        }
        TokenStorage.save(context, "oauthId", oauthId)
        TokenStorage.save(context, "nickname", nickname)
        TokenStorage.save(context, "profile", profileImageUrl ?: "")

        val loginRequest = LoginRequest(idToken = idToken!!, oauthId = oauthId)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.getAuthService().login(loginRequest)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("LoginScreen", "로그인 성공")
                        val tokenResponse = response.body()
                        if (tokenResponse != null) {
                            TokenStorage.save(context, tokenResponse)
                        } else {
                            Log.e("LoginScreen", "token is null")
                        }
                        // 환영 페이지로 이동
                        navController.navigate("welcome")
                    } else if (response.code() == 401) {
                        Log.d("LoginScreen", "로그인 실패")
                        handleLoginFailure(context, navController, oauthId, idToken, response)
                    } else {
                        Log.e("LoginScreen", "Unexpected response: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("LoginScreen", "로그인 요청 실패", e)
            }
        }
    }

    /**
     * 로그인 실패 상황을 처리하는 함수.
     * - 에러 응답에서 code 값을 파싱하여 상황별로 분기 처리
     * - AUTH004(탈퇴 후 재가입)일 경우 재가입 요청 후 처리
     * - 그 외의 경우 회원가입 화면으로 이동
     */
    private fun handleLoginFailure(
        context: Context,
        navController: NavController,
        oauthId: String,
        idToken: String,
        response: retrofit2.Response<*>
    ) {
        // 에러 응답에서 code 값을 파싱
        val errorResponse = response.errorBody()?.string()
        val errorCode = try {
            JSONObject(errorResponse ?: "{}").getString("code")
        } catch (e: Exception) {
            null
        }

        // AUTH004: 탈퇴 후 재가입 요청 상황 처리 (6개월 이내 탈퇴 이력 안내 모달)
        if (errorCode == "AUTH004") {
            AlertDialog.Builder(context)
                .setTitle("재가입 안내")
                .setMessage("6개월 이내 탈퇴 이력이 있습니다. 재가입하시겠습니까?")
                .setPositiveButton("확인") { _, _ ->
                    val retryRequest = LoginRequest(
                        oauthId = oauthId,
                        idToken = idToken,
                        reSignUp = true
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        val retryResponse = RetrofitClient.getAuthService().login(retryRequest)
                        withContext(Dispatchers.Main) {
                            if (retryResponse.isSuccessful) {
                                val token = retryResponse.body()
                                if (token != null) {
                                    TokenStorage.save(context, token)
                                    navController.navigate("welcome") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            } else {
                                navController.navigate("login")
                            }
                        }
                    }
                }
                .setNegativeButton("취소") { _, _ ->
                    navController.navigate("login")
                }
                .show()
        } else {
            // 그 외 코드인 경우 회원가입 화면으로 이동
            Log.d("LoginScreen", "회원 가입 필요: $errorResponse")
            navController.navigate("signup")
        }
    }

    fun loginWithKakao(
        context: Context,
        onSuccess: (nickname: String?, profileImageUrl: String?, oauthId: String, idToken: String?) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("KakaoLogin", "로그인 실패", error)
                onFailure(error)
            } else if (token != null) {
                Log.d("KakaoLogin", "로그인 성공: ${token.accessToken}")
                fetchUserInfo(token, onSuccess, onFailure)
            }
        }

        val activity = context as? Activity
        if (activity != null && UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(activity) { token, error ->
                if (error != null) {
                    Log.e("KakaoLogin", "카카오톡 로그인 실패", error)
                    if (error is com.kakao.sdk.common.model.ClientError &&
                        error.reason == com.kakao.sdk.common.model.ClientErrorCause.Cancelled) {
                        // 사용자가 취소한 경우
                        return@loginWithKakaoTalk
                    } else {
                        // 실패했지만 취소가 아닌 경우 이메일 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                    }
                } else if (token != null) {
                    Log.d("KakaoLogin", "카카오톡 로그인 성공: ${token.accessToken}")
                    fetchUserInfo(token, onSuccess, onFailure)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }

    private fun fetchUserInfo(
        token: OAuthToken,
        onSuccess: (String?, String?, String, String?) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("KakaoLogin", "사용자 정보 요청 실패", error)
                onFailure(error)
            } else if (user != null) {
                val nickname = user.kakaoAccount?.profile?.nickname
                val profileImageUrl = user.kakaoAccount?.profile?.profileImageUrl
                val oauthId = user.id.toString()
                val idToken = token.idToken

                Log.d("KakaoLogin", "닉네임: ${nickname}, 프로필: ${profileImageUrl}, ID: ${oauthId}, ID Token: ${idToken}")
                onSuccess(nickname, profileImageUrl, oauthId, idToken)
            }
        }
    }
}