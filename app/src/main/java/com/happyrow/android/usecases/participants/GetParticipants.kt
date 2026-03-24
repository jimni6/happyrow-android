package com.happyrow.android.usecases.participants

import com.happyrow.android.domain.model.Participant
import com.happyrow.android.domain.repository.ParticipantRepository
import javax.inject.Inject

class GetParticipants @Inject constructor(
    private val participantRepository: ParticipantRepository
) {
    suspend operator fun invoke(eventId: String): List<Participant> {
        require(eventId.isNotBlank()) { "Event ID is required" }
        return participantRepository.getParticipantsByEvent(eventId)
    }
}
