package com.happyrow.android.domain.model

data class Contribution(
    val id: String,
    val eventId: String,
    val resourceId: String,
    val userId: String,
    val quantity: Int,
    val createdAt: Long
)

data class ContributionCreationRequest(
    val eventId: String,
    val resourceId: String,
    val userId: String,
    val quantity: Int
)

data class ContributionUpdateRequest(
    val quantity: Int? = null
)
