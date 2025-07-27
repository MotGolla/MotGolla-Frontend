package com.motgolla.ui.screen.record

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.motgolla.util.PreferenceUtil

@Composable
fun RecordScreen(navController: NavHostController) {
    val context = LocalContext.current
    // 상태로 백화점 이름 저장 (초기값은 null or "알 수 없음"으로 둬도 좋아요)
    //예시화면
    val departmentName = remember { mutableStateOf("불러오는 중...") }

    // 컴포저블이 처음 실행될 때 SharedPreference에서 읽어오기
    LaunchedEffect(Unit) {
        val savedName = PreferenceUtil.getDepartmentName(context)
        departmentName.value = savedName ?: "알 수 없음"
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("✅ RecordScreen 입니다!\n백화점: ${departmentName.value}")
    }
    Button(onClick = { navController.navigate("shoppingRecord") }) {
        Text(text = "쇼핑 기록 화면으로 이동")
    }

}
