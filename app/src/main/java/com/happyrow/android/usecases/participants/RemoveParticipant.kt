package com.happyrow.android.usecases.participants

import com.happyrow.android.domain.repository.ParticipantRepository
import javax.inject.Inject

class RemoveParticipant @Inject constructor(
    private val participantRepository: ParticipantRepository
) {
    suspend operator fun invoke(eventId: String, userEmail: String) {
        require(eventId.isNotBlank()) { "Event ID is required" }
        require(userEmail.isNotBlank()) { "User email is required" }
        participantRepository.removeParticipant(eventId, userEmail)
    }
}
