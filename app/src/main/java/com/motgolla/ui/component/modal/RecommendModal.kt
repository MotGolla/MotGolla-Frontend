package com.motgolla.ui.component.modal

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.motgolla.common.RetrofitClient
import com.motgolla.common.storage.TokenStorage
import com.motgolla.domain.recommend.data.ProductPreview
import com.motgolla.ui.component.item.ProductPreviewList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendModal(
    productId: Long,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var productList by remember { mutableStateOf<List<ProductPreview>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val nickname = TokenStorage.getValue(context, "nickname") ?: "사용자"

    LaunchedEffect(productId) {
        coroutineScope.launch {
            try {
                val result = RetrofitClient.getRecordService().getRecommendedProducts(productId)
                productList = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 딤 + 시트 분리 구성
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)) // 딤 처리 완전 수동
            .clickable(onClick = onDismissRequest)
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        scrimColor = Color.Transparent // 내부 scrim은 제거
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

            ProductPreviewList(productList, 5)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecommendModalPreview() {
    RecommendModal(1L, onDismissRequest = {})
}