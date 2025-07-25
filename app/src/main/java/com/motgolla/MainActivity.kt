package com.motgolla

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.motgolla.ui.theme.MotgollaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MotgollaTheme {
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
