package com.happyrow.android.domain.model

data class AuthSession(
    val user: User,
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long
)
