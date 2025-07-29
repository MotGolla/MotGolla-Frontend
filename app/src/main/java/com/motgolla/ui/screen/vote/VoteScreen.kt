package com.motgolla.ui.screen.vote

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VoteScreen(
    selectedTab: VoteTab,
    voteCards: List<VoteCard>,
    onTabSelect: (VoteTab) -> Unit,
    onSelectCandidate: (Long, Long) -> Unit,
    onSubmitVote: (Long) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 🔹 탭 UI
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VoteTab.values().forEach { tab ->
                val isSelected = tab == selectedTab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { onTabSelect(tab) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = tab.label,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // 🔹 투표 목록
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(voteCards) { card ->
                VoteCardItem(
                    card = card,
                    onSelectCandidate = onSelectCandidate,
                    onSubmitVote = onSubmitVote
                )
            }
        }
    }
}
