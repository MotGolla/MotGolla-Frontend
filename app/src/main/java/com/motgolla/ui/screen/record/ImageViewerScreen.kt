package com.motgolla.ui.screen.record


import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageViewerScreen(
    navController: NavController,
    imageListStr: String,
    initialIndex: Int
) {
    val imageUrls = remember(imageListStr) {
        Uri.decode(imageListStr).split(",")
    }

    val pagerState = rememberPagerState(initialPage = initialIndex, pageCount = { imageUrls.size })

    Box(modifier = Modifier.fillMaxSize()) {
        // 슬라이드 이미지 with pinch-zoom
        HorizontalPager(state = pagerState) { page ->
            var scale by remember { mutableStateOf(1f) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)
                        }
                    }
            ) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrls[page]),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                )
            }
        }

        // 닫기 버튼
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "닫기",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        // 페이지 표시
        Text(
            text = "${pagerState.currentPage + 1}/${imageUrls.size}",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageViewerPreviewScreen(
    imageListStr: String,
    initialIndex: Int
) {
    val imageUrls = remember(imageListStr) {
        Uri.decode(imageListStr).split(",")
    }

    val pagerState = rememberPagerState(initialPage = initialIndex, pageCount = { imageUrls.size })

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState) { page ->
            var scale by remember { mutableStateOf(1f) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)
                        }
                    }
            ) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrls[page]),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                )
            }
        }

        Text(
            text = "${pagerState.currentPage + 1}/${imageUrls.size}",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}



@Preview(showBackground = true)
@Composable
fun ImageViewerScreenPreview() {
    val dummyImages = listOf(
        "https://hix-bucket.s3.ap-northeast-2.amazonaws.com/R1.JPG",
        "https://hix-bucket.s3.ap-northeast-2.amazonaws.com/R1-2.JPG",
        "https://hix-bucket.s3.ap-northeast-2.amazonaws.com/tag1.webp"
    )
    val imageListStr = Uri.encode(dummyImages.joinToString(","))

    ImageViewerPreviewScreen(
        imageListStr = imageListStr,
        initialIndex = 0
    )
}

