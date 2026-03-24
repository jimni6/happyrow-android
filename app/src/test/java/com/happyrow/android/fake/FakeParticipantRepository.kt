package com.happyrow.android.fake

import com.happyrow.android.TestData
import com.happyrow.android.domain.model.Participant
import com.happyrow.android.domain.model.ParticipantCreationRequest
import com.happyrow.android.domain.model.ParticipantUpdateRequest
import com.happyrow.android.domain.repository.ParticipantRepository

class FakeParticipantRepository : ParticipantRepository {

    val participants = mutableListOf<Participant>()
    var shouldThrow: Exception? = null

    override suspend fun addParticipant(data: ParticipantCreationRequest): Participant {
        shouldThrow?.let { throw it }
        val p = TestData.participant(userEmail = data.userEmail, eventId = data.eventId, status = data.status)
        participants.add(p)
        return p
    }

    override suspend fun getParticipantsByEvent(eventId: String): List<Participant> {
        shouldThrow?.let { throw it }
        return participants.filter { it.eventId == eventId }
    }

    override suspend fun updateParticipantStatus(eventId: String, userEmail: String, data: ParticipantUpdateRequest): Participant {
        shouldThrow?.let { throw it }
        val idx = participants.indexOfFirst { it.eventId == eventId && it.userEmail == userEmail }
        if (idx == -1) throw NoSuchElementException("Participant not found")
        val updated = participants[idx].copy(status = data.status)
        participants[idx] = updated
        return updated
    }

    override suspend fun removeParticipant(eventId: String, userEmail: String) {
        shouldThrow?.let { throw it }
        participants.removeAll { it.eventId == eventId && it.userEmail == userEmail }
    }
}
