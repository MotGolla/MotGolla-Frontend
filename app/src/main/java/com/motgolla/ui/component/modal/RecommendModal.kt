package com.motgolla.ui.component.modal

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.motgolla.R
import com.motgolla.common.RetrofitClient
import com.motgolla.common.storage.TokenStorage
import com.motgolla.domain.recommend.data.RecommendedProduct
import com.motgolla.ui.component.item.ItemSmallBox
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendModal(
    productId: Long,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    var productList by remember { mutableStateOf<List<RecommendedProduct>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val nickname = TokenStorage.getValue(context, "nickname") ?: "사용자"

    LaunchedEffect(productId) {
        coroutineScope.launch {
            try {
                val result = RetrofitClient.getRecordService().getRecommendedProducts(productId)
                productList = result
                Log.d("RecommendModal", "productList: $productList")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "${nickname}님을 위한 추천 아이템",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp)
            )

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
                    items(productList) { product ->
                        ItemSmallBox(product = product)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecommendModalPreview() {
    RecommendModal(1L, onDismissRequest = {})
}