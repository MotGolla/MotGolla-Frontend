package com.motgolla.domain.vote.data.request

data class VoteCreateRequest(
    val title: String,
    val recordIds: List<Long>
)