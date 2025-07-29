package com.motgolla.ui.screen.vote

data class VoteCard(
    val id: Long,
    val nickname: String,
    val profileImage: String,
    val timeAgo: String,
    val question: String,
    val candidates: List<VoteCandidate>,
    val hasVoted: Boolean
)