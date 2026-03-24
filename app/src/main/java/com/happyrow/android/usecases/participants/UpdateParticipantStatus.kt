package com.happyrow.android.usecases.participants

import com.happyrow.android.domain.model.Participant
import com.happyrow.android.domain.model.ParticipantUpdateRequest
import com.happyrow.android.domain.repository.ParticipantRepository
import javax.inject.Inject

class UpdateParticipantStatus @Inject constructor(
    private val participantRepository: ParticipantRepository
) {
    suspend operator fun invoke(
        eventId: String,
        userEmail: String,
        request: ParticipantUpdateRequest
    ): Participant {
        require(eventId.isNotBlank()) { "Event ID is required" }
        require(userEmail.isNotBlank()) { "User email is required" }
        return participantRepository.updateParticipantStatus(eventId, userEmail, request)
    }
}
