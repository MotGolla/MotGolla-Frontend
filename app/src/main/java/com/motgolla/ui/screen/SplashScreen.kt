package com.motgolla.ui.screen

import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.motgolla.R
import com.motgolla.common.RetrofitClient
import kotlinx.coroutines.delay
//
//@Composable
//fun SplashScreen(navController: NavHostController) {
//    val composition by rememberLottieComposition(
//        LottieCompositionSpec.RawRes(R.raw.splash)
//    )
//
//    val progress by animateLottieCompositionAsState(
//        composition = composition,
//        iterations = 1, // 한 번만 재생
//        speed = 1f
//    )
//
//    // 애니메이션 끝났을 때 다음 화면으로 이동
//    LaunchedEffect(progress) {
//        if (progress == 1f) {
//            delay(300) // 약간의 여유
//            navigateByLoginState(navController)
//        }
//    }
//
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        LottieAnimation(
//            composition = composition,
//            progress = { progress },
//            modifier = Modifier.size(250.dp)
//        )
//    }
//}

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


//흰디로 하고 싶다.. But...
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
            .background(Color(0xFFFFFEFC)), // ← 배경색 지정
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(R.drawable.test2)
                .build(),
            contentDescription = "Splash GIF",
            imageLoader = imageLoader,
            modifier = Modifier.size(250.dp)
        )
    }
}
