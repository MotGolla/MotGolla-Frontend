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
import androidx.navigation.NavController
import com.motgolla.common.RetrofitClient
import com.motgolla.domain.record.api.repository.ShoppingHistoryRepository
import com.motgolla.viewmodel.record.ShoppingHistoryViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: LocationViewModel = viewModel()
) {

    var initDone by remember { mutableStateOf(false) }

    val shoppingHistoryViewModel = remember {
        ShoppingHistoryViewModel(
            ShoppingHistoryRepository(RetrofitClient.getRecordService())
        )
    }
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
                        0.0f to Color(0xFF8d0aff),
                        0.1f to Color(0xFF700aff),
                        0.22f to Color(0xFF4a12ff)
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
                .fillMaxHeight(0.78f)
                .align(Alignment.BottomCenter),
            shoppingHistoryViewModel = shoppingHistoryViewModel,
            navController = navController
        )
    }
}