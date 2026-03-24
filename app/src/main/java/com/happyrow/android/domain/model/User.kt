package com.happyrow.android.domain.model

data class User(
    val id: String,
    val email: String,
    val emailConfirmed: Boolean,
    val firstname: String,
    val lastname: String,
    val createdAt: Long,
    val updatedAt: Long,
    val metadata: Map<String, Any?>? = null
)
