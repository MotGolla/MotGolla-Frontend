package com.motgolla.ui.screen.home.top

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motgolla.ui.screen.home.DepartmentSelectionModal

import com.motgolla.viewmodel.LocationViewModel

@Composable
fun LocationSection(viewModel: LocationViewModel) {
    val departmentName by viewModel.departmentName
    val locationChanged by viewModel.locationChanged

    var showModal by remember { mutableStateOf(false) }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
        ) {
            if (locationChanged) {
                Text(
                    text = "백화점 위치가 맞나요? 위치를 갱신해주세요!",
                    color = Color.Yellow,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
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
                imageVector = Icons.Default.ExpandMore,
                contentDescription = "백화점 수동 선택",
                tint = Color.White,
                modifier = Modifier.clickable {
                    showModal = true
                }
            )
        }
    }

    DepartmentSelectionModal(
        isVisible = showModal,
        onDismiss = { showModal = false },
        onManualDepartmentSelected = { selected ->
            viewModel.selectDepartmentManually(selected)
        },
        onUseGPS = {
            viewModel.forceUpdateFromGPS()
        }
    )
}

