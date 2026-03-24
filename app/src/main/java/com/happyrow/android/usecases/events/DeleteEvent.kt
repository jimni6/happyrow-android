package com.happyrow.android.usecases.events

import com.happyrow.android.domain.repository.EventRepository
import javax.inject.Inject

class DeleteEvent @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(id: String, userId: String) {
        require(id.isNotBlank()) { "Event ID is required" }
        require(userId.isNotBlank()) { "User ID is required" }
        eventRepository.deleteEvent(id, userId)
    }
}
