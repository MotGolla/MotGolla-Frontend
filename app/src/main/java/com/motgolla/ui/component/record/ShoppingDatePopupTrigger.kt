package com.motgolla.ui.component.record

import ShoppingDateCalendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShoppingDatePopupTrigger(
    selectedDate: LocalDate,
    availableDates: List<LocalDate>,
    onDateSelected: (LocalDate) -> Unit,
    onMonthOpened: (YearMonth) -> Unit //
) {
    var showCalendarDialog by remember { mutableStateOf(false) }

    val formatter = remember { DateTimeFormatter.ofPattern("yy.MM.dd") }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = "calendar",
            tint = Color.Black,
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    showCalendarDialog = true
                }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = selectedDate.format(formatter),
            fontSize = 18.sp,
            color = Color.Black
        )
    }

    if (showCalendarDialog) {
        Dialog(
            onDismissRequest = { showCalendarDialog = false },
            properties = DialogProperties(
                dismissOnClickOutside = true,
                dismissOnBackPress = true
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize() // 전체 화면을 차지하는 투명 배경
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        showCalendarDialog = false
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ShoppingDateCalendar(
                        availableDates = availableDates,
                        onDateSelected = {
                            onDateSelected(it)
                            showCalendarDialog = false
                        },
                        onMonthChanged = onMonthOpened
                    )
                }
            }
        }
    }
}
