package com.motgolla.ui.screen.home.bottom

import com.motgolla.ui.component.item.ShoppingHistoryBox
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.motgolla.viewmodel.record.ShoppingHistoryViewModel

@Composable
fun ShoppingHistorySection(
    viewModel: ShoppingHistoryViewModel,
    navController: NavController
) {
    val products by remember { mutableStateOf(viewModel.products) }

    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(
            title = "오늘의 쇼핑 기록",
            onClickMore = {
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(products) { product ->
                ShoppingHistoryBox(
                    product = product,
                    onClick = {
                        //  상세 페이지로 이동 (recordId 기반)
                        navController.navigate("record_detail/${product.recordId}")
                    }
                )
            }
        }
    }
}