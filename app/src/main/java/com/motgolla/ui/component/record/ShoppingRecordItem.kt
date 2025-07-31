package com.motgolla.ui.component.record

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import com.motgolla.domain.record.data.response.ShoppingRecordInfoResponse


@Composable
fun ShoppingRecordItem(
    item: ShoppingRecordInfoResponse,
    onCompleteClicked: (Long) -> Unit,
    onItemClicked: (Long) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { onItemClicked(item.recordId) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 좌측 컬럼: 상태 배지 + 이미지
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(end = 12.dp)
                .offset(y = (-10).dp)
        ) {
            // 상태 배지 (이미지 바깥 좌상단)
            Text(
                text = if ("COMPLETED".equals(item.state)) "구매완료" else "구매보류",
                color = if ("COMPLETED".equals(item.state)) Color.Gray else MaterialTheme.colorScheme.primary,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .offset(y = (-6).dp)
                    .width(80.dp)              // 가로는 그대로
                    .height(90.dp)            // 세로를 늘림
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White)
            ) {
                // painter = rememberAsyncImagePainter(model = item.imgUrl)
                // https://image.msscdn.net/thumbnails/images/goods_img/20250429/5076776/5076776_17494260822630_big.png?w=1200
                Image(
                    painter = rememberAsyncImagePainter(model = item.imgUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(6.dp))
                )
            }
        }
        // 우측 정보들
        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "${item.brandName} • ${item.brandFloor}",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.productName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                lineHeight = 18.sp,  // 줄간격 조정
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.productPrice,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )


                CompleteButton(
                    state = item.state,
                    onClick = { onCompleteClicked(item.recordId) }
                )
            }
        }
    }
}

@Composable
fun CompleteButton(
    state: String,
    onClick: () -> Unit
) {
    val isCompleted = state == "COMPLETED"
    val backgroundColor = Color(0xFFFFFFFF)
    val borderColor = Color(0xFFF0F1F3)
    val textColor = if (isCompleted) Color(0xFFCED3DB) else Color(0xFF222B45)
    val buttonText = if (isCompleted) "구매 완료" else "구매 완료하기"

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(
                enabled = !isCompleted,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .width(90.dp)    // 원하는 너비 고정 (예시: 90.dp)
            .height(28.dp)   // 원하는 높이 고정 (예시: 28.dp)
            .padding(horizontal = 0.dp, vertical = 0.dp), // 패딩 제거
        contentAlignment = Alignment.Center            // 텍스트 중앙 정렬
    ) {
        Text(
            text = buttonText,
            fontWeight = FontWeight.Medium,
            fontSize = 9.sp,
            color = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingRecordItemPreview() {
    ShoppingRecordItem(
        item = ShoppingRecordInfoResponse(
            recordId = 1L,
            state = "PENDING",
            imgUrl = "",
            productName = "샘플 제품 이름이 길어도 두 줄까지 보여줍니다.",
            brandName = "테스트브랜드",
            brandFloor = "3F",
            productPrice = "19,900원"
        ),
        onCompleteClicked = {},
        onItemClicked = {}
    )
}
