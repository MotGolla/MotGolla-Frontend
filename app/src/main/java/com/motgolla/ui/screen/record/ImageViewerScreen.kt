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
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.geometry.Offset

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
    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { imageUrls.size }
    )
    // 각 페이지의 스케일 상태 저장
    val scaleStates = remember { mutableStateListOf<Float>().apply {
        repeat(imageUrls.size) { add(1f) }
    }}

    Box(Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            // 스케일 > 1 인 경우만 슬라이드 비활성화
            userScrollEnabled = scaleStates[pagerState.currentPage] <= 1f
        ) { page ->
            ZoomableImage(
                imageUrl = imageUrls[page],
                onTransform = { newScale ->
                    scaleStates[page] = newScale
                }
            )
        }

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = "닫기")
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

@Composable
private fun ZoomableImage(
    imageUrl: String,
    minScale: Float = 1f,
    maxScale: Float = 5f,
    onTransform: (Float) -> Unit
) {
    var scale by remember { mutableStateOf(minScale) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // forEachGesture 블록 안에서는 detectTransformGestures를 안전하게 호출할 수 있습니다.
                forEachGesture {
                    // 1) 두 손가락 이상이 내려올 때까지 대기
                    awaitPointerEventScope {
                        // 첫 번째 포인터 down 대기
                        awaitFirstDown()
                        // 두 번째 포인터가 올 때까지 계속 기다림
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.changes.size >= 2) break
                            // 만약 포인터가 다 올라가 버리면 이 제스처 루프를 끝냄
                            if (event.changes.all { it.pressed.not() }) return@awaitPointerEventScope
                        }
                    }
                    // 2) 이제 두 손가락 이상일 때만 핀치 줌/패닝 처리
                    detectTransformGestures { _, pan, zoom, _ ->
                        val newScale = (scale * zoom).coerceIn(minScale, maxScale)
                        scale = newScale
                        offset = if (newScale > minScale) offset + pan else Offset.Zero
                        onTransform(scale)
                    }
                }
            }
            // 변환 결과 적용
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationX = offset.x
                translationY = offset.y
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
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
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
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

