package com.happyrow.android.usecases.participants

import com.happyrow.android.domain.model.Participant
import com.happyrow.android.domain.model.ParticipantCreationRequest
import com.happyrow.android.domain.repository.ParticipantRepository
import javax.inject.Inject

class AddParticipant @Inject constructor(
    private val participantRepository: ParticipantRepository
) {
    suspend operator fun invoke(request: ParticipantCreationRequest): Participant {
        require(request.eventId.isNotBlank()) { "Event ID is required" }
        require(request.userEmail.isNotBlank()) { "Email is required" }
        require(EMAIL_REGEX.matches(request.userEmail)) { "Invalid email format" }
        return participantRepository.addParticipant(request)
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
