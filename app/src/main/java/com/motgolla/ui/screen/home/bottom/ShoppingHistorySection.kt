package com.motgolla.ui.screen.home.bottom

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.motgolla.R
import com.motgolla.ui.component.item.ShoppingHistoryBox
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
                navController.navigate("record")
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (products.isEmpty()) {
            // 상품이 없을 경우 빈 이미지 출력
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.record_empty),
                    contentDescription = "추가 아이콘",
                    modifier = Modifier.size(140.dp)
                )
            }
        } else {
            // 상품 리스트 출력
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(products) { product ->
                    ShoppingHistoryBox(
                        product = product,
                        onClick = {
                            navController.navigate("record_detail/${product.recordId}")
                        }
                    )
                }
            }
        }
    }
}
