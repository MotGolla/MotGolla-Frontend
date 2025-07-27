package com.motgolla

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kakao.sdk.common.KakaoSdk

class KakaoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Kakao SDK 초기화
        AndroidThreeTen.init(this)
        KakaoSdk.init(this, BuildConfig.KAKAO_APP_KEY)
    }
}