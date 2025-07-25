package com.motgolla.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.motgolla.ui.screen.home.bottom.BottomBox
import com.motgolla.ui.screen.home.top.TopBox

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to Color(0xFFAA80F9),
                        0.6f to Color(0xFF6165D7),
                        1.0f to Color(0xFF6165D7)
                    )
                )
            )
            .padding(top = 40.dp)
    ) {
        TopBox()

        // 하단 박스
        BottomBox(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.77f)
                .align(Alignment.BottomCenter)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
