package com.motgolla.ui.screen.vote

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.motgolla.viewmodel.vote.VoteViewModel

@Composable
fun VoteScreenWrapper() {
    val viewModel = remember { VoteViewModel() }
    val voteCards by viewModel.voteCards
    val selectedTab = remember { mutableStateOf(VoteTab.ALL) }

    LaunchedEffect(Unit) {
        viewModel.loadVotes("ALL")
    }

    VoteScreen(
        selectedTab = selectedTab.value,
        voteCards = voteCards,
        onTabSelect = { tab ->
            selectedTab.value = tab
            viewModel.loadVotes(tab.name)
        },
        onSelectCandidate = { voteGroupId, candidateId ->
            viewModel.selectCandidate(voteGroupId, candidateId)
        },
        onSubmitVote = { voteGroupId ->
            viewModel.submitVote(voteGroupId)
        }
    )
}