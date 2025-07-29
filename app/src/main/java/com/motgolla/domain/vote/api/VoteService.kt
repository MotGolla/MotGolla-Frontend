package com.motgolla.domain.vote.api

import com.motgolla.domain.vote.data.request.VoteActionRequest
import com.motgolla.domain.vote.data.request.VoteCreateRequest
import com.motgolla.domain.vote.data.response.ProductResponse
import com.motgolla.domain.vote.data.response.VoteCreateResponse
import com.motgolla.domain.vote.data.response.VoteDetailResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface VoteService {
    @POST("/api/votes")
    suspend fun createVote(@Body request: VoteCreateRequest): Response<VoteCreateResponse>

    @GET("/api/votes")
    suspend fun getVotes(@Query("type") type: String): Response<List<VoteDetailResponse>>

    @POST("/api/votes/{voteGroupId}")
    suspend fun vote(
        @Path("voteGroupId") voteGroupId: Long,
        @Body request: VoteActionRequest)

    @GET("/api/record/products")
    suspend fun getProducts(
        @Query("date") date: String,
        @Query("category") category: String,
        @Query("cursor") cursor: Long?,
        @Query("limit") limit: Int
    ): Response<ProductResponse>

}