package com.motgolla.ui.screen.record

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.motgolla.R
import com.motgolla.domain.record.data.response.ShoppingRecordInfoResponse
import com.motgolla.ui.component.record.ShoppingRecordItem
import com.motgolla.ui.component.record.data.DummyShoppingData
import com.motgolla.viewmodel.record.RecordViewModel

@Composable
fun ShoppingRecordListScreen(
    navController: NavHostController,
    recordViewModel: RecordViewModel,
    records: List<ShoppingRecordInfoResponse>,
    isPagingLoading: Boolean,
    selectedCategory: String,
    selectedDate: String,
    onLoadMore: () -> Unit
) {
    var dialogRecordId by remember { mutableStateOf<Long?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.Top)
                ) {
                    Text(
                        text = "쇼핑, 남기면 더 편해요.",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "사진만 올리면, 기록 끝! 투표도 함께해요.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier.padding(start = 20.dp)
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                ) {
                                    append("쇼핑 기록")
                                }
                                append(" 등록 >")
                            },
                            fontSize = 12.sp,
                            modifier = Modifier.clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                navController.navigate("shoppingRecord")
                            }
                        )

                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = Color(0xFF7B2CBF),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                ) {
                                    append("투표")
                                }
                                append(" 등록 >")
                            },
                            fontSize = 12.sp,
                            modifier = Modifier.clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                navController.navigate("vote/productSelect?date=$selectedDate")
                            }
                        )
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.record_shopping),
                    contentDescription = "캐릭터",
                    modifier = Modifier
                        .size(110.dp)
                        .align(Alignment.Top)
                )
            }

            Divider(
                color = Color(0xFFEAEAEA),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )

            ShoppingRecordLazyList(
                records = records,
                onLoadMore = onLoadMore,
                isPagingLoading = isPagingLoading,
                onCompleteClicked = { recordId ->
                    dialogRecordId = recordId
                },
                onItemClicked = { recordId ->
                    navController.navigate("record_detail/$recordId")
                }
            )
        }

        // 오버레이 다이얼로그
        if (dialogRecordId != null) {
            ConfirmCompleteDialog(
                onConfirm = {
                    recordViewModel.completeRecord(dialogRecordId!!, selectedCategory, selectedDate)
                    dialogRecordId = null
                },
                onCancel = {
                    dialogRecordId = null
                }
            )
        }
    }
}

@Composable
fun ShoppingRecordLazyList(
    records: List<ShoppingRecordInfoResponse>,
    onLoadMore: () -> Unit,
    isPagingLoading: Boolean,
    onCompleteClicked: (Long) -> Unit,
    onItemClicked: (Long) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState, records.size) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex == records.lastIndex && !isPagingLoading) {
                    onLoadMore()
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = records,
            key = { it.recordId }
        ) { item ->
            ShoppingRecordItem(
                item = item,
                onCompleteClicked = onCompleteClicked,
                onItemClicked = onItemClicked
            )
            Divider(color = Color(0xFFEAEAEA), thickness = 1.dp)
        }

        if (isPagingLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF7B2CBF),
                        strokeWidth = 4.dp
                    )
                }
            }
        } else {
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun ConfirmCompleteDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(300.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("구매 완료하시겠습니까?", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("등록하신 상품 기록들을 천천히 둘러보시고 고민해 보세요.", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF7B2CBF))
                    .clickable { onConfirm() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("구매완료하기", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "취소",
                color = Color.Gray,
                fontSize = 13.sp,
                modifier = Modifier.clickable { onCancel() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingRecordListScreenPreview() {
    val fakeViewModel = RecordViewModel()
    ShoppingRecordListScreen(
        navController = rememberNavController(),
        recordViewModel = fakeViewModel,
        records = DummyShoppingData.shoppingRecords,
        onLoadMore = {},
        isPagingLoading = true,
        selectedCategory = "전체",
        selectedDate = "2025-07-28"
    )
}
