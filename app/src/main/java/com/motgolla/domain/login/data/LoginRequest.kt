package com.motgolla.domain.login.data

data class LoginRequest(
    val idToken: String,
    val oauthId: String,
    val reSignUp: Boolean = false
)
