package com.motgolla.ui.screen.vote

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motgolla.viewmodel.vote.VoteCreateViewModel
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun VoteProductSelectScreen(
    viewModel: VoteCreateViewModel,
    date: String,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val products = viewModel.products
    val selectedIds = viewModel.selectedRecordIds
    val isLoading = viewModel.isLoading
    val hasNext = viewModel.hasNext
    val canProceed by viewModel.canProceed

    // 최초 로딩
    LaunchedEffect(date) {
        viewModel.loadProducts(date)
    }

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    if (viewModel.canProceedToNext()) {
                        onNext()
                    } else {
                        Toast.makeText(context, "2개 이상 4개 이하를 선택해주세요.", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = canProceed,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (viewModel.selectedRecordIds.size >= 2) MaterialTheme.colorScheme.primary else Color.Gray
                )
            ) {
                Text("다음", color = Color.White, style = MaterialTheme.typography.bodyMedium)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("투표")
                    }
                    withStyle(style = SpanStyle(color = Color.Black)) {
                        append("에 올릴 상품들을 선택해주세요!")
                    }
                },
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(50.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("최대 4개까지 선택이 가능해요", fontSize = 12.sp, color = Color.DarkGray)
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products) { product ->
                    ProductCardItem(
                        product = product,
                        isSelected = viewModel.selectedRecordIds.contains(product.record_id),
                        onClick = { viewModel.toggleRecordId(product.record_id) }
                    )
                }
                if (isLoading) {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }

    // 무한 스크롤 감지
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastIndex ->
                if (lastIndex == products.lastIndex && hasNext && !isLoading) {
                    viewModel.loadProducts(date)
                }
            }
    }

}