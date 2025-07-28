package com.motgolla.domain.notification.api.service

import android.content.Context
import android.util.Log
import com.motgolla.common.storage.TokenStorage
import com.motgolla.domain.notification.api.repository.FcmTokenRepository

object NotificationService {

    fun updateNotificationSetting(context: Context, checked: Boolean) {
        TokenStorage.save(context, "notificationCheck", checked.toString())

        val token = TokenStorage.getFcmToken(context)
        val accessToken = TokenStorage.getTokenResponse(context)?.accessToken

        if (!token.isNullOrEmpty() && !accessToken.isNullOrEmpty()) {
            if (checked) {
                FcmTokenRepository().sendTokenToServer(token, accessToken)
                Log.d("NotificationSetting", "알림 ON: 서버에 토큰 등록")
            } else {
                FcmTokenRepository().deleteTokenFromServer(token, accessToken)
                Log.d("NotificationSetting", "알림 OFF: 서버에서 토큰 삭제")
            }
        } else {
            Log.w("NotificationSetting", "FCM 토큰이나 AccessToken이 없음")
        }
    }
}
