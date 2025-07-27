package com.motgolla.domain.record.view.ui

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.motgolla.R

@Composable
fun MemoItem(viewModel: MemoViewModel) {
    // 상태 수집
    val memo by viewModel.memo.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val recordingTime by viewModel.recordingTime.collectAsState()

    val context = LocalContext.current

    // 퍼미션 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                viewModel.startRecording() // 실시간 STT 호출
            } else {
                Toast.makeText(
                    context,
                    "음성 녹음 권한이 필요합니다",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )

    // UI
    OutlinedTextField(
        value = memo,
        onValueChange = viewModel::setMemo,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        label = { Text("메모") },
        placeholder = {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "음성으로 남기면 AI가 요약해드려요.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color(0xFFE6E8EB)
        ),
        maxLines = 3,
        trailingIcon = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(bottom = 4.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom
            ) {
                if (isRecording) {
                    Text(
                        text = recordingTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Red,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                IconButton(
                    onClick = {
                        if (!isRecording) {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        } else {
                            viewModel.stopRecording()
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.record_microphone),
                        contentDescription = if (isRecording) "녹음 중지" else "음성 입력",
                        tint = if (isRecording) Color.Red else Color.Unspecified
                    )
                }
            }
        }
    )
}
