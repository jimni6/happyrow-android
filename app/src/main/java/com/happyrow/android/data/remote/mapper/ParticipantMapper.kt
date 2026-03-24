package com.happyrow.android.data.remote.mapper

import com.happyrow.android.data.remote.dto.ParticipantApiRequest
import com.happyrow.android.data.remote.dto.ParticipantApiResponse
import com.happyrow.android.domain.model.Participant
import com.happyrow.android.domain.model.ParticipantCreationRequest
import com.happyrow.android.domain.model.ParticipantStatus
import com.happyrow.android.domain.model.ParticipantUpdateRequest

fun ParticipantApiResponse.toDomain(): Participant = Participant(
    userEmail = user_email,
    userName = user_name,
    eventId = event_id,
    status = runCatching { ParticipantStatus.valueOf(status.uppercase()) }
        .getOrDefault(ParticipantStatus.INVITED),
    joinedAt = joined_at,
    updatedAt = updated_at
)

fun ParticipantCreationRequest.toApiRequest(): ParticipantApiRequest = ParticipantApiRequest(
    user_email = userEmail,
    status = status.name
)

fun ParticipantUpdateRequest.toApiRequest(userEmail: String): ParticipantApiRequest =
    ParticipantApiRequest(
        user_email = userEmail,
        status = status.name
    )
