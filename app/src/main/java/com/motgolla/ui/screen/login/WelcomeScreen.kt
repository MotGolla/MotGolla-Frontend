package com.motgolla.ui.screen.login

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import kotlinx.coroutines.delay
import androidx.navigation.compose.rememberNavController

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.motgolla.R

@Composable
fun WelcomeScreen(navController: NavController) {
    val context = LocalContext.current

    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data("android.resource://${context.packageName}/raw/welcome")  // raw의 gif 파일
            .size(Size.ORIGINAL)
            .build(),
        imageLoader = imageLoader
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFAF9))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painter,
                contentDescription = "Welcome GIF",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
    LaunchedEffect(Unit) {
        delay(1700)
        navController.navigate("home") {
            popUpTo("welcome") { inclusive = true }
        }
    }
}
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreenPreviewOnly()
}

@Composable
fun WelcomeScreenPreviewOnly() {
    val dummyNavController = rememberNavController()
    WelcomeScreen(navController = dummyNavController)
}