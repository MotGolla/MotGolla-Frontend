package com.motgolla.ui.screen

import android.os.Build
import android.util.Log

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.motgolla.R
import com.motgolla.common.RetrofitClient
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    val context = LocalContext.current

    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    LaunchedEffect(Unit) {
        delay(4000)
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true }
        }
        navigateByLoginState(navController)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFEFC)),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(R.drawable.test2)
                .build(),
            contentDescription = "Splash GIF",
            imageLoader = imageLoader,
            modifier = Modifier.size(300.dp)
        )
    }
}


/**
 * 로그인 상태에 따른 페이지 이동 함수
 * 토큰 유효: 홈 화면으로 이동
 * 토큰 만료: 로그인 화면 이동
 * 예외 감지: 로그인 화면 이동
 */
private suspend fun navigateByLoginState(navController: NavHostController) {
    try {
        val response = RetrofitClient.getAuthService().getMemberInfo()
        if (response.isSuccessful) {
            Log.d("SplashScreen", "Login Member : $response")
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            Log.d("SplashScreen", "Need Login")
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    } catch (e: Exception) {
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    val fakeNavController = rememberNavController()
    SplashScreen(navController = fakeNavController)
}