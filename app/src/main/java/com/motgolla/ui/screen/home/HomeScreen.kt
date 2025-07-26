package com.motgolla.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.motgolla.ui.common.RequestLocationPermission
import com.motgolla.ui.screen.home.bottom.BottomBox
import com.motgolla.ui.screen.home.top.TopBox
import androidx.lifecycle.viewmodel.compose.viewModel
import com.motgolla.viewmodel.LocationViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Composable
fun HomeScreen(viewModel: LocationViewModel = viewModel()) {

    var initDone by remember { mutableStateOf(false) }

    //앱 시작 시 위치 불러오기
    LaunchedEffect(Unit) {
        viewModel.initPreviousLocation {
            viewModel.checkIfLocationChangedOnEnter()
            initDone = true
        }
    }

    if (initDone) {
        RequestLocationPermission {
            viewModel.startLocationUpdates()
        }
    }

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
        TopBox(viewModel = viewModel)

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
