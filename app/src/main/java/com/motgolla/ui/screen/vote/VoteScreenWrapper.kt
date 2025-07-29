package com.motgolla.ui.screen.vote

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.motgolla.viewmodel.vote.VoteViewModel

@Composable
fun VoteScreenWrapper(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel = remember { VoteViewModel() }
    val voteCards by viewModel.voteCards
    val selectedTab = remember { mutableStateOf(VoteTab.ALL) }

    LaunchedEffect(Unit) {
        viewModel.loadVotes("ALL")
    }

    VoteScreen(
        navController = navController,
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
            if (!viewModel.selectedCandidateMap.containsKey(voteGroupId)) {
                Toast.makeText(context, "투표할 상품을 선택해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.submitVote(
                    voteGroupId = voteGroupId,
                    onSuccess = {
                        Toast.makeText(context, "투표가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    },
                    onError = {
                        Toast.makeText(context, "투표 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    )
}