package com.motgolla.ui.component.item

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.motgolla.domain.record.data.response.ShoppingHistoryProduct

@Composable
fun ShoppingHistoryBox(product: ShoppingHistoryProduct) {
    Log.d("ShoppingHistoryBox", "product: $product")

    val imageUrl = product.imgUrl?.takeIf { it.isNotBlank() }
        ?: "android.resource://com.motgolla/drawable/sample"

    val floorLabel = product.brandFloor ?: ""
    val priceText = product.price ?: ""

    Column(
        modifier = Modifier
            .width(120.dp)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,  // 이미지 꽉 채우기
            modifier = Modifier
                .height(140.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 텍스트들 컨테이너
        Column(modifier = Modifier.padding(horizontal = 4.dp)) {
            val productName = product.productName ?: ""
            Text(
                text = if (productName.length > 17) productName.take(17) + "..." else productName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            val brandName = product.brandName ?: ""
            Text(
                text = brandName,
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = floorLabel,
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = priceText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }
        }
    }
}
