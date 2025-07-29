package com.motgolla.domain.vote.data.response

import java.time.LocalDateTime

data class VoteDetailResponse(
    val voteGroupId: Long,
    val title: String,
    val profileImage: String,
    val nickname: String,
    val timeAgo: String,
    val mine: Boolean,
    val votedByMe: Boolean,
    val candidates: List<CandidateResult>
)

data class CandidateResult(
    val candidateId: Long,
    val recordId: Long,
    val voteCount: Int,
    val totalVotes: Int,
    val percentage: Int?, // null if hidden
    val imageUrl: String
)