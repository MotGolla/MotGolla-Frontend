import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CategoryFilterRow(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
//    val categories = listOf("전체", "상의", "니트/가디건", "아우터", "팬츠", "스커트")

    val categories = listOf("전체", "반소매 티셔츠", "카디건", "바지", "코트", "머플러")

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(64.dp)
                    .height(30.dp)
                    .background(
                        color = if (isSelected) Color(0xFF834FF1) else Color(0xFFF1F1F1),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { onCategorySelected(category) }
            ) {
                Text(
                    text = category,
                    color = if (isSelected) Color.White else Color.Black,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

