package com.happyrow.android.data.repository

import com.happyrow.android.data.remote.api.ParticipantApi
import com.happyrow.android.data.remote.mapper.toApiRequest
import com.happyrow.android.data.remote.mapper.toDomain
import com.happyrow.android.domain.model.Participant
import com.happyrow.android.domain.model.ParticipantCreationRequest
import com.happyrow.android.domain.model.ParticipantUpdateRequest
import com.happyrow.android.domain.repository.ParticipantRepository
import javax.inject.Inject

class ParticipantRepositoryImpl @Inject constructor(
    private val participantApi: ParticipantApi
) : ParticipantRepository {

    override suspend fun addParticipant(data: ParticipantCreationRequest): Participant {
        return participantApi.addParticipant(
            eventId = data.eventId,
            request = data.toApiRequest()
        ).toDomain()
    }

    override suspend fun getParticipantsByEvent(eventId: String): List<Participant> {
        return participantApi.getParticipantsByEvent(eventId).map { it.toDomain() }
    }

    override suspend fun updateParticipantStatus(
        eventId: String,
        userEmail: String,
        data: ParticipantUpdateRequest
    ): Participant {
        return participantApi.updateParticipantStatus(
            eventId = eventId,
            userEmail = userEmail,
            request = data.toApiRequest(userEmail)
        ).toDomain()
    }

    override suspend fun removeParticipant(eventId: String, userEmail: String) {
        participantApi.removeParticipant(eventId, userEmail)
    }
}
