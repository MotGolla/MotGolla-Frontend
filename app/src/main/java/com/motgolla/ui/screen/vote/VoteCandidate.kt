package com.motgolla.ui.screen.vote

data class VoteCandidate(
    val id: Long,
    val imageUrl: String,
    val voteRatio: Int?, // null이면 투표 전, 숫자면 투표 후
    val isSelected: Boolean = false
)