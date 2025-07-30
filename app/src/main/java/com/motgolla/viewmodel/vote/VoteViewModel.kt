package com.motgolla.viewmodel.vote

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motgolla.domain.vote.api.VoteRepository
import com.motgolla.domain.vote.data.response.VoteDetailResponse
import com.motgolla.ui.screen.vote.VoteCandidate
import com.motgolla.ui.screen.vote.VoteCard
import kotlinx.coroutines.launch

class VoteViewModel(
    private val voteRepository: VoteRepository = VoteRepository()
) : ViewModel() {

    var voteCards = mutableStateOf<List<VoteCard>>(emptyList())
        private set

    var selectedCandidateMap = mutableMapOf<Long, Long>()

    fun loadVotes(type: String = "ALL") {
        viewModelScope.launch {
            try {
                val response = voteRepository.getVotes(type)
                if (response.isSuccessful) {
                    val list = response.body()?.map {
                        val selectedId = selectedCandidateMap[it.voteGroupId]
                        it.toVoteCard(selectedId) // ✅ 전달
                    } ?: emptyList()
                    voteCards.value = list
                }
            } catch (e: Exception) {
                // 네트워크 에러 처리
            }
        }
    }

    fun selectCandidate(voteGroupId: Long, candidateId: Long) {
        val updatedCards = voteCards.value.map { card ->
            if (card.id == voteGroupId) {
                card.copy(
                    candidates = card.candidates.map {
                        it.copy(isSelected = it.id == candidateId)
                    }
                )
            } else card
        }
        voteCards.value = updatedCards
        selectedCandidateMap[voteGroupId] = candidateId
    }

    fun submitVote(
        voteGroupId: Long,
        onSuccess: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        val candidateId = selectedCandidateMap[voteGroupId] ?: return
        viewModelScope.launch {
            try {
                voteRepository.vote(voteGroupId, candidateId)
                onSuccess()
                loadVotes("ALL")
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    private fun VoteDetailResponse.toVoteCard(selectedId: Long?): VoteCard {
        return VoteCard(
            id = voteGroupId,
            nickname = nickname,
            profileImage = profileImage ?: "",
            timeAgo = timeAgo,
            question = title,
            hasVoted = votedByMe,
            candidates = candidates.map { candidate ->
                VoteCandidate(
                    id = candidate.candidateId,
                    recordId = candidate.recordId,
                    imageUrl = candidate.imageUrl,
                    voteRatio = candidate.percentage,
                    isSelected = candidate.candidateId == selectedId
                )
            }
        )
    }

}