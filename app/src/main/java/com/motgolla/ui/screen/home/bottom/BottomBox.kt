package com.motgolla.ui.screen.home.bottom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.motgolla.viewmodel.record.ShoppingHistoryViewModel
@Composable
fun BottomBox(
    modifier: Modifier = Modifier,
    shoppingHistoryViewModel: ShoppingHistoryViewModel,
    navController: NavHostController
) {
    BoxWithConstraints(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                clip = false
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            )
    ) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(
                    horizontal = screenWidth * 0.05f, // 좌우 패딩 5%로 비율 조정
                    vertical = screenHeight * 0.04f    // 상하 패딩도 비율로
                )
        ) {
            DepartmentStoreSection(navController = navController)
            Spacer(modifier = Modifier.height(screenHeight * 0.02f))
            ShoppingHistorySection(
                viewModel = shoppingHistoryViewModel,
                navController = navController
            )
        }
    }
}
