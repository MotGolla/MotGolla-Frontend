package com.motgolla.domain.login.api.service

import android.app.Activity
import android.content.Context
import android.util.Log
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient

object KakaoLoginService {

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