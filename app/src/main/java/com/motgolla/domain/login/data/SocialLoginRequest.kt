package com.motgolla.domain.login.data

data class SocialLoginRequest(
    val idToken: String,
    val oauthId: String,
    val reSignUp: Boolean = false
)
