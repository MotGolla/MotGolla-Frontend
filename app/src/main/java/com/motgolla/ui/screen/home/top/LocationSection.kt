package com.motgolla.ui.screen.home.top

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.motgolla.viewmodel.LocationViewModel

@Composable
fun LocationSection(viewModel: LocationViewModel) {
    //백화점 이름과 위치 변경 여부
    val departmentName by viewModel.departmentName
    val locationChanged by viewModel.locationChanged

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = departmentName,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.GpsFixed,
                contentDescription = "Location Target",
                tint = Color.White,
                modifier = Modifier.clickable {
                    viewModel.updateLocationManually()
                }
            )
        }

        if (locationChanged) {
            Text(
                text = "백화점 위치가 맞나요? 아이콘을 눌러 갱신해주세요!",
                color = Color.Yellow,
                fontSize = 14.sp,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}
