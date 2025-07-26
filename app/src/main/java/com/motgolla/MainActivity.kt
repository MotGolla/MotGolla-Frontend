package com.motgolla

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.motgolla.common.RetrofitClient
import com.motgolla.ui.theme.MotgollaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Android 12 이상 네이티브 스플래시스크린 설치
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContent {
            MotgollaTheme {
                val navController = rememberNavController()
                RetrofitClient.init(this, navController)
                MotGollaApp()  // 메인 UI 구성
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MotGollaAppPreview() {
    MotGollaApp()
}
