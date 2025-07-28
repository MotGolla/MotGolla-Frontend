package com.motgolla

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kakao.sdk.common.KakaoSdk
import com.motgolla.common.RetrofitClient
import com.motgolla.common.storage.TokenStorage
import com.motgolla.domain.notification.api.repository.FcmTokenRepository
import com.motgolla.ui.theme.MotgollaTheme
import androidx.activity.viewModels
import com.motgolla.viewmodel.record.MemoViewModel
import com.motgolla.viewmodel.record.RecordViewModel
import com.motgolla.ui.screen.record.ShoppingRecordScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        // FCM 토큰 받아서 서버에 저장
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM_TOKEN", "FCM 받은 토큰: $token")

                TokenStorage.saveFcmToken(this, token)

                // 저장된 accessToken 가져오기 (SharedPreferences나 저장소에서)
                val accessToken = TokenStorage.getTokenResponse(this)?.accessToken
                if (!accessToken.isNullOrEmpty()) {
                    FcmTokenRepository().sendTokenToServer(token, accessToken)
                } else {
                    Log.w("FCM_TOKEN", "accessToken 없음. 로그인 후 토큰 전송 필요")
                }
            } else {
                Log.e("FCM_TOKEN", "FCM 토큰 가져오기 실패", task.exception)
            }
        }


        setContent {
            MotgollaTheme {
                val navController = rememberNavController()
                RetrofitClient.init(this, navController)
                MotGollaApp()  // 메인 UI 구성
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION", "알림 권한 허용됨")
            } else {
                Log.d("PERMISSION", "알림 권한 거부됨")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MotGollaAppPreview() {
    MotGollaApp()
}
