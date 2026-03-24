package com.happyrow.android.domain.model

enum class ParticipantStatus { INVITED, CONFIRMED, MAYBE, DECLINED }

data class Participant(
    val userEmail: String,
    val userName: String? = null,
    val eventId: String,
    val status: ParticipantStatus,
    val joinedAt: Long,
    val updatedAt: Long? = null
)

data class ParticipantCreationRequest(
    val eventId: String,
    val userEmail: String,
    val status: ParticipantStatus
)

data class ParticipantUpdateRequest(
    val status: ParticipantStatus
)
