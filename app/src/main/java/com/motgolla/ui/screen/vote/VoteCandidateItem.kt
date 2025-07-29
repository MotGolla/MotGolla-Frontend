package com.motgolla.ui.screen.vote

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter

@Composable
fun VoteCandidateItem(
    candidate: VoteCandidate,
    isVoted: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (candidate.isSelected && !isVoted) 2.dp else 1.dp,
                color = if (candidate.isSelected && !isVoted) MaterialTheme.colorScheme.primary else Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = !isVoted) { onClick() }
    ) {
        if (candidate.imageUrl.isNotBlank()) {
            AsyncImage(
                model = candidate.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("No Image", color = Color.Gray, fontSize = 12.sp)
            }
        }

        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "상세보기",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(16.dp),
            tint = Color.DarkGray
        )

        if (candidate.voteRatio != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // 정사각형 이미지
                    .clip(RoundedCornerShape(8.dp))
            ) {
                // 1. 이미지
                Image(
                    painter = rememberAsyncImagePainter(candidate.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                // 2. 득표율 오버레이
                candidate.voteRatio?.let { ratio ->
                    val ratioFraction = ratio / 100f

                    // 전체 어둡게 (투명 회색)
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                    )

                    // 득표율 비율만큼 아래에서부터 올라오는 박스
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(ratioFraction)
                            .align(Alignment.BottomCenter)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)) // 보라 계열
                    )

                    // 텍스트
                    Text(
                        text = "$ratio%",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VoteCandidateItemPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("득표율 없는 경우", fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
        VoteCandidateItem(
            candidate = VoteCandidate(
                id = 1,
                imageUrl = "https://flexible.img.hani.co.kr/flexible/normal/700/1040/imgdb/original/2021/0428/20210428504000.jpg",
                voteRatio = null
            ),
            isVoted = false,
            onClick = {}
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("득표율 60%", fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
        VoteCandidateItem(
            candidate = VoteCandidate(
                id = 2,
                imageUrl = "https://flexible.img.hani.co.kr/flexible/normal/700/1040/imgdb/original/2021/0428/20210428504000.jpg",
                voteRatio = 60
            ),
            isVoted = true,
            onClick = {}
        )
    }
}