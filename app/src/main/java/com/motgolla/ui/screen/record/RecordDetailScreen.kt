package com.motgolla.ui.screen.record

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.motgolla.domain.record.data.response.RecordDetailResponse
import com.motgolla.ui.component.ErrorScreen
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.derivedStateOf
import androidx.navigation.NavController
import com.motgolla.common.storage.TokenStorage
import com.motgolla.domain.recommend.data.ProductPreview
import com.motgolla.domain.recommend.service.RecommendService
import com.motgolla.ui.component.item.ProductPreviewList
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.window.DialogProperties


@Composable
fun RecordDetailScreen(
    recordId: Long,
    navController: NavController,
    viewModel: RecordDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(recordId) {
        viewModel.loadRecord(recordId)
    }

    when {
        uiState.errorMessage != null -> ErrorScreen(uiState.errorMessage!!)
        uiState.record != null -> RecordDetailContent(
            record = uiState.record!!,
            navController = navController
        )
        else -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun RecordDetailContent(
    record: RecordDetailResponse,
    navController: NavController? = null
) {
    val context = LocalContext.current
    val allImages = remember(record.imageUrls, record.tagImageUrl) {
        record.imageUrls + listOfNotNull(record.tagImageUrl)
    }

    var showMap by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val activeIndex by remember {
        derivedStateOf {
            val offset = listState.firstVisibleItemScrollOffset
            val index = listState.firstVisibleItemIndex
            if (offset > 150) index + 1 else index
        }
    }

    var enlargedImageUrl by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            // 이미지 슬라이더
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allImages) { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .width(300.dp)
                                .fillMaxHeight()
                                .clickable {
                                    navController?.let {
                                        val index = allImages.indexOf(imageUrl)
                                        val encodedImages = Uri.encode(allImages.joinToString(","))
                                        it.navigate("image_viewer/${encodedImages}/$index")
                                    }
                                },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }

        item {
            // 슬라이드 인디케이터
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(allImages.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .padding(3.dp)
                            .background(
                                if (index == activeIndex) Color(0xFF7E57C2) else Color.Gray,
                                CircleShape
                            )
                    )
                }
            }
        }

        item {
            // 상태 & 날짜 & 지도 버튼
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    StatusBox(
                        text = "구매 ${if (record.productStatus == "COMPLETED") "완료" else "보류"}",
                        bgColor = Color(0xFF7E57C2),
                        textColor = Color.White
                    )

                    StatusBox(
                        text = record.recordCreatedAt,
                        bgColor = Color(0xFFEDE7F6),
                        textColor = Color.Gray
                    )
                }

                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .width(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { showMap = true },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "지도 보기",
                            tint = Color.Gray
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(text = record.productName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "%,d원".format(record.productPrice),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                InfoAlignedRow("브랜드", record.brandName)
                InfoAlignedRow("모델명", record.productNumber)
                InfoAlignedRow("사이즈", record.productSize)
                InfoAlignedRow("백화점 지점", record.storeName)
                InfoAlignedRow("매장 위치", record.brandLocationInfo)
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(text = "메모", fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color(0xFFF6F6F6), shape = RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(text = record.productSummary, fontSize = 14.sp)
                }
            }
        }

        item {
            RecommendRow(context, record.productId)
        }
    }

    if (showMap) {
        Dialog(
            onDismissRequest = { showMap = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnClickOutside = true
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.9f)
                ) {
                    Column {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showMap = false }) {
                                Text("닫기")
                            }
                        }
                        AndroidView(factory = {
                            WebView(it).apply {
                                webViewClient = WebViewClient()
                                settings.javaScriptEnabled = true
                                loadUrl(record.storeMapLink)
                            }
                        }, modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}


@Composable
fun InfoAlignedRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.width(96.dp)  // 고정 너비로 정렬 맞춤
        )
        Text(text = value)
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(text = "$label: ", fontWeight = FontWeight.SemiBold)
        Text(text = value)
    }
}

@Composable
fun StatusBox(text: String, bgColor: Color, textColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(bgColor)
            .height(32.dp) // 고정 높이
//            .width(IntrinsicSize.Min)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = textColor, fontSize = 14.sp, maxLines = 1,)
    }
}

@Composable
fun RecommendRow(context: Context, productId: Long) {
    val nickname = TokenStorage.getValue(context, "nickname") ?: "사용자"
    var productList by remember { mutableStateOf<List<ProductPreview>>(emptyList()) }
    val recommendService = remember { RecommendService() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(productId) {
        coroutineScope.launch {
            productList = recommendService.getRecommendedProducts(productId)
        }
    }

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
        )

        ProductPreviewList(productList, 5)
    }
}


@Preview(showBackground = true)
@Composable
fun RecordDetailScreenPreview() {
    val dummyRecord = RecordDetailResponse(
        recordId = 1,
        productId = 1,
        productName = "Women Bold Fox Head Patch Regular Cardigan",
        brandName = "메종키츠네",
        productPrice = 535000,
        recordCreatedAt = "2025-07-27 18:21",
        productSize = "L",
        productNumber = "MW00508KT1036-H218",
        productStatus = "AVAILABLE",
        productSummary = "재질이 좋다",
        brandLocationInfo = "B2",
        imageUrls = listOf(
            "https://hix-bucket.s3.ap-northeast-2.amazonaws.com/R1.JPG",
            "https://hix-bucket.s3.ap-northeast-2.amazonaws.com/R1-2.JPG"
        ),
        tagImageUrl = "https://hix-bucket.s3.ap-northeast-2.amazonaws.com/tag1.webp",
        storeName = "현대백화점 압구정본점",
        mapLink = "https://place.map.kakao.com/21297272",
        storeMapLink = "https://place.map.kakao.com/2006302169"
    )

    RecordDetailContent(
        record = dummyRecord
    )
}
