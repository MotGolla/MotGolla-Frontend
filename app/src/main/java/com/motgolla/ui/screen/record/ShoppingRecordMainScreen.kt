package com.motgolla.ui.screen.record

import CategoryFilterRow
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.motgolla.ui.component.record.ShoppingDatePopupTrigger
import com.motgolla.util.PreferenceUtil
import com.motgolla.viewmodel.record.RecordViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShoppingRecordMainScreen(
    navController: NavHostController,
    recordViewModel: RecordViewModel
) {
    val context = LocalContext.current
    val departmentName = remember { mutableStateOf("불러오는 중...") }
    var selectedCategory by remember { mutableStateOf("전체") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val availableDates by recordViewModel.availableRecordDates.collectAsState()
    val shoppingList by recordViewModel.shoppingRecordInfoList.collectAsState()


    val isInitialLoading by recordViewModel.isInitialLoading.collectAsState()
    val isPagingLoading by recordViewModel.isPagingLoading.collectAsState()


    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // 최초 진입 시 부서명 + 초기 데이터 요청
    LaunchedEffect(Unit) {
        val savedName = PreferenceUtil.getDepartmentName(context)
        departmentName.value = savedName ?: "알 수 없음"

        recordViewModel.fetchInitialShoppingRecords(
            category = selectedCategory,
            date = selectedDate.format(dateFormatter)
        )
    }

    // 날짜나 카테고리 변경 시 데이터 추가 요청
    LaunchedEffect(selectedCategory, selectedDate) {
        recordViewModel.resetAndFetch(
            category = selectedCategory,
            date = selectedDate.format(dateFormatter)
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // 달력 월별 필터
        ShoppingDatePopupTrigger(
            selectedDate = selectedDate,
            availableDates = availableDates,
            onDateSelected = {
                selectedDate = it
                recordViewModel.resetAndFetch(selectedCategory, it.format(dateFormatter))
            },
            onMonthOpened = { yearMonth ->
                val formatted = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"))
                recordViewModel.fetchRecordDatesByYearMonth(formatted)
            }
        )

        // 카테고리 필터
        CategoryFilterRow(
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )

        if (isInitialLoading) {
            LoadingScreen() // Cover all
        } else {
            ShoppingRecordListScreen(
                navController = navController,
                recordViewModel = recordViewModel,
                records = shoppingList,
                selectedCategory = selectedCategory,
                selectedDate = selectedDate.format(dateFormatter),
                isPagingLoading = isPagingLoading,
                onLoadMore = {
                    recordViewModel.fetchMoreShoppingRecords(selectedCategory, selectedDate.format(dateFormatter))
                }
            )
        }
    }
}
@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = Color(0xFF7B2CBF), // 원하는 색상 지정
            strokeWidth = 4.dp // 선 두께
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}