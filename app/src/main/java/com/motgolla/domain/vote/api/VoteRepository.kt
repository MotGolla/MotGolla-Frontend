package com.motgolla.domain.vote.api

import com.motgolla.common.RetrofitClient
import com.motgolla.domain.vote.data.request.VoteActionRequest
import com.motgolla.domain.vote.data.request.VoteCreateRequest
import com.motgolla.domain.vote.data.response.ProductResponse
import com.motgolla.domain.vote.data.response.VoteCreateResponse
import com.motgolla.domain.vote.data.response.VoteDetailResponse
import retrofit2.Response

class VoteRepository {

    private val voteService = RetrofitClient.getVoteService()

    suspend fun createVote(request: VoteCreateRequest): Response<VoteCreateResponse> {
        return voteService.createVote(request)
    }

    suspend fun getVotes(type: String): Response<List<VoteDetailResponse>> {
        return voteService.getVotes(type)
    }

    suspend fun vote(voteGroupId: Long, voteCandidateId: Long) {
        voteService.vote(
            voteGroupId = voteGroupId,
            request = VoteActionRequest(voteCandidateId = voteCandidateId)
        )
    }

    suspend fun getProducts(date: String, category: String, cursor: Long?, limit: Int): Response<ProductResponse> {
        return voteService.getProducts(
            date = date,
            category = category,
            cursor = cursor,
            limit = limit
        )
    }
}