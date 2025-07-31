package com.motgolla.domain.login.data

data class NormalSignUpRequest(
    val oauthId: String,
    val password: String,
    val name: String,
    val gender: String,
    val birthday: String,
    val profile: String?
)