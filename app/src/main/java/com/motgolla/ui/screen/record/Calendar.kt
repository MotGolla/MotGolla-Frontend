package com.motgolla.ui.screen.record

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.threeten.bp.LocalDate

@Composable
fun CustomCalendar(
    markedDates: List<LocalDate>,
    onDateClick: (LocalDate) -> Unit
) {
    val today = remember { LocalDate.now() }
    val startOfMonth = today.withDayOfMonth(1)
    val endOfMonth = today.withDayOfMonth(today.lengthOfMonth())

    val daysInMonth = (0 until endOfMonth.dayOfMonth).map { startOfMonth.plusDays(it.toLong()) }

    Column {
        Text(
            text = "${today.month} ${today.year}",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            content = {
                items(daysInMonth.size) { index ->
                    val date = daysInMonth[index]
                    val isMarked = markedDates.contains(date)

                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isMarked) Color(0xFF2FC8AB) else Color.LightGray)
                            .clickable(enabled = isMarked) {
                                onDateClick(date)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            color = if (isMarked) Color.White else Color.DarkGray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        )
    }
}