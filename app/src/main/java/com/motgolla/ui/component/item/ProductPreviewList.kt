package com.motgolla.ui.component.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.motgolla.R
import com.motgolla.domain.recommend.data.ProductPreview

@Composable
fun ProductPreviewList(productList: List<ProductPreview>, limit: Int){
    if (productList.isEmpty()) {
        // 비어 있을 때 이미지 표시
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.error),
                contentDescription = "추천 상품 없음",
                modifier = Modifier.size(160.dp)
            )
            Text(
                text = "상품이 없어요...",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    } else {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productList.take(limit)) { product ->
                ItemSmallBox(product = product)
            }
        }
    }
}