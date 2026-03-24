package com.happyrow.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ContributionApiRequest(
    val quantity: Int
)

@Serializable
data class ContributionApiResponse(
    val identifier: String,
    val participant_id: String,
    val resource_id: String,
    val quantity: Int,
    val created_at: Long,
    val updated_at: Long? = null
)
