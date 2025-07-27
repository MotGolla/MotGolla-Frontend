package com.motgolla.ui.component.record

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.motgolla.R
import java.io.File

@Composable
fun RecordImageItem(file: File, onDelete: (File) -> Unit) {
    Box(
        Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Image(
            painter = rememberAsyncImagePainter(file),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )
        // 삭제 버튼을 IconButton으로 변경하여 터치 영역을 보장합니다.
        IconButton(
            onClick = { onDelete(file) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(14.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.record_delete),
                contentDescription = "삭제",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
