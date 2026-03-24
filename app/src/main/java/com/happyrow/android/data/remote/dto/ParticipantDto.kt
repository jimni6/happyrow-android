package com.happyrow.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ParticipantApiRequest(
    val user_email: String,
    val status: String
)

@Serializable
data class ParticipantApiResponse(
    val user_email: String,
    val user_name: String? = null,
    val event_id: String,
    val status: String,
    val joined_at: Long,
    val updated_at: Long? = null
)
