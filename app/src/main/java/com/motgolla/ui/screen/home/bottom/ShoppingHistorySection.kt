package com.motgolla.ui.screen.home.bottom

import com.motgolla.ui.component.item.ShoppingHistoryBox
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.motgolla.viewmodel.record.ShoppingHistoryViewModel

@Composable
fun ShoppingHistorySection(viewModel: ShoppingHistoryViewModel) {
    val products by remember { mutableStateOf(viewModel.products) }

    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(
            title = "오늘의 쇼핑 기록",
            onClickMore = {
                // 이동할 페이지 넣기
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(products) { product ->
                ShoppingHistoryBox(product)
            }
        }
    }
}