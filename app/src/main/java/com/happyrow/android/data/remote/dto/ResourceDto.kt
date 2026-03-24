package com.happyrow.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResourceApiRequest(
    val name: String,
    val category: String,
    val quantity: Int,
    val suggested_quantity: Int? = null
)

@Serializable
data class ContributorApiResponse(
    val user_id: String,
    val quantity: Int,
    val contributed_at: Long
)

@Serializable
data class ResourceApiResponse(
    val identifier: String,
    val event_id: String,
    val name: String,
    val category: String,
    val current_quantity: Int,
    val suggested_quantity: Int? = null,
    val contributors: List<ContributorApiResponse> = emptyList(),
    val version: Int = 0,
    val created_at: Long,
    val updated_at: Long? = null
)
