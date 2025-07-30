package com.motgolla.domain.login.data

data class SocialSignUpRequest(
    val idToken: String,
    val oauthId: String,
    val name: String,
    val gender: String,
    val birthday: String,
    val profile: String
)