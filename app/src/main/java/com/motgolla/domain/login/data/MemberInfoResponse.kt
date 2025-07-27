package com.motgolla.domain.login.data

data class MemberInfoResponse(
    val id: Long,
    val name: String,
    val birthday: String,
    val gender: String,
    val profile: String?,       // nullable로 변경
    val createdAt: String?      // nullable로 변경
)
