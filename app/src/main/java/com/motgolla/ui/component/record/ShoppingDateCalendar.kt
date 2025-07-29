// build.gradle(:app)에 추가
// implementation "com.kizitonwose.calendar:compose:1.0.0"

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.daysOfWeek
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShoppingDateCalendar(
    availableDates: List<LocalDate>,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (YearMonth) -> Unit
) {
    val today = remember { LocalDate.now() }
    val startMonth = remember { YearMonth.from(today.minusMonths(3)) }
    val endMonth = remember { YearMonth.from(today.plusMonths(3)) }
    val daysOfWeek = remember { daysOfWeek() }
    var selectedDate by remember { mutableStateOf(today) }
    val coroutineScope = rememberCoroutineScope()

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = YearMonth.from(today),
        firstDayOfWeek = daysOfWeek.first()
    )

    val previousMonthState = remember { mutableStateOf<YearMonth?>(null) }

    LaunchedEffect(calendarState.firstVisibleMonth.yearMonth) {
        val newMonth = calendarState.firstVisibleMonth.yearMonth
        if (newMonth != previousMonthState.value) {
            previousMonthState.value = newMonth
            onMonthChanged(newMonth) // 실제 API 트리거
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🔼 월 네비게이션과 타이틀
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Previous month",
                modifier = Modifier
                    .clickable {
                        coroutineScope.launch {
                            calendarState.animateScrollToMonth(
                                calendarState.firstVisibleMonth.yearMonth.minusMonths(1)
                            )
                        }
                    }
                    .padding(8.dp)
            )
            Text(
                text = calendarState.firstVisibleMonth.yearMonth.format(DateTimeFormatter.ofPattern("yyyy년 M월")),
                fontSize = 18.sp,
                color = Color(0xFF00796B),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Next month",
                modifier = Modifier
                    .clickable {
                        coroutineScope.launch {
                            calendarState.animateScrollToMonth(
                                calendarState.firstVisibleMonth.yearMonth.plusMonths(1)
                            )
                        }
                    }
                    .padding(8.dp)
            )
        }

        // 요일 헤더
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f),
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalCalendar(
            state = calendarState,
            dayContent = { day ->
                val date = day.date
                val isAvailable = availableDates.contains(date)
                val isSelected = date == selectedDate

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(6.dp)
                        .then(
                            if (isAvailable) Modifier.background(
                                color = if (isSelected) Color(0xFF7B2CBF) else Color(0xFFEADDFF),
                                shape = CircleShape
                            ) else Modifier
                        )
                        .clickable(enabled = isAvailable) {
                            selectedDate = date
                            onDateSelected(date)
                        }
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        color = if (isAvailable) Color.Black else Color.LightGray,
                        fontSize = 14.sp
                    )
                }
            }
        )
    }
}
