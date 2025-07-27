package com.motgolla.ui.component.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motgolla.R
import com.motgolla.domain.recommend.data.RecommendedProduct

@Composable
fun ItemSmallBox(product: RecommendedProduct) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.sample),
            contentDescription = null,
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            product.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        Text(
            product.brand,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${product.floor}F", fontSize = 13.sp)
            Text("%,d".format(product.price), fontSize = 13.sp)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ItemSmallBoxPreview() {
    ItemSmallBox(RecommendedProduct(1, "미쏘", "에센셜 브이넥 가디건", 4, 19900))
}