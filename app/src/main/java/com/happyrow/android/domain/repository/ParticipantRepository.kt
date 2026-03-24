package com.happyrow.android.domain.repository

import com.happyrow.android.domain.model.Participant
import com.happyrow.android.domain.model.ParticipantCreationRequest
import com.happyrow.android.domain.model.ParticipantUpdateRequest

interface ParticipantRepository {
    suspend fun addParticipant(data: ParticipantCreationRequest): Participant
    suspend fun getParticipantsByEvent(eventId: String): List<Participant>
    suspend fun updateParticipantStatus(
        eventId: String,
        userEmail: String,
        data: ParticipantUpdateRequest
    ): Participant
    suspend fun removeParticipant(eventId: String, userEmail: String)
}
