package com.motgolla.ui.component.item

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.motgolla.domain.recommend.data.ProductPreview

@Composable
fun ItemSmallBox(product: ProductPreview) {
    val imageUrl = product.imageUrl?.takeIf { it.isNotBlank() }
        ?: "android.resource://com.motgolla/drawable/sample"
    val floorLabel = if (product.floor < 0) {
        "B${-product.floor}"
    } else {
        "${product.floor}F"
    }

    Column(
        modifier = Modifier
            .width(140.dp)
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(6.dp)) // 기존 12.dp → 6.dp로 살짝만 둥글게
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (product.name.length > 17) product.name.take(17) + "..." else product.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        Text(
            text = product.brand.take(20),
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = floorLabel,
                fontSize = 13.sp
            )
            Text(
                text = String.format("%,d원", product.price),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ItemSmallBoxPreview() {
    ItemSmallBox(ProductPreview(1, "미쏘", "에센셜 브이넥 가디건", -2, 19900, ""))
}