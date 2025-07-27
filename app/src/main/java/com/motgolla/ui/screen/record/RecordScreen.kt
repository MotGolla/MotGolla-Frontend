package com.motgolla.ui.screen.record

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.threeten.bp.LocalDate

@Composable
fun RecordScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val markedDates = try {
            listOf(
                LocalDate.of(2025, 7, 3),
                LocalDate.of(2025, 7, 10),
                LocalDate.of(2025, 7, 25)
            )
        } catch (e: Exception) {
            emptyList()
        }

        CustomCalendar(markedDates = markedDates) { selectedDate ->
            println("클릭된 날짜: $selectedDate")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecordScreenPreview() {
    RecordScreen()
}