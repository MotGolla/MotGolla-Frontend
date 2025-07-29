package com.motgolla.ui.screen.vote

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@Composable
fun VoteCardItem(
    card: VoteCard,
    onSelectCandidate: (cardId: Long, candidateId: Long) -> Unit,
    onSubmitVote: (cardId: Long) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF8F8F8))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter(card.profileImage), // 프로필 이미지 URL
                contentDescription = "프로필",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(card.nickname, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(card.timeAgo, fontSize = 12.sp, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append("Q. ")
                }
                withStyle(style = SpanStyle(color = Color.Black)) {
                    append("${card.question}")
                }
            },
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false
        ) {
            items(card.candidates) { candidate ->
                VoteCandidateItem(
                    candidate = candidate,
                    isVoted = card.hasVoted,
                    onClick = {
                        onSelectCandidate(card.id, candidate.id)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { onSubmitVote(card.id) },
            enabled = !card.hasVoted,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (card.hasVoted) Color.LightGray else MaterialTheme.colorScheme.primary,
                contentColor = if (card.hasVoted) Color.DarkGray else Color.White
            )
        ) {
            Text(if (card.hasVoted) "투표완료" else "투표하기")
        }
    }
}