package com.motgolla.ui.screen.home.bottom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.motgolla.viewmodel.record.ShoppingHistoryViewModel

@Composable
fun BottomBox(
    modifier: Modifier = Modifier,
    shoppingHistoryViewModel: ShoppingHistoryViewModel
) {
    Box(
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, top = 24.dp)
        ) {
            DepartmentStoreSection()
            Spacer(modifier = Modifier.height(22.dp))
            ShoppingHistorySection(shoppingHistoryViewModel)
        }
    }
}