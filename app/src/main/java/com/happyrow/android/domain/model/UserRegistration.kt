package com.happyrow.android.domain.model

data class UserRegistration(
    val email: String,
    val password: String,
    val firstname: String,
    val lastname: String,
    val confirmPassword: String? = null,
    val metadata: Map<String, Any?>? = null
)
