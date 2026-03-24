package com.happyrow.android.usecases.events

import com.happyrow.android.domain.model.Event
import com.happyrow.android.domain.model.EventCreationRequest
import com.happyrow.android.domain.repository.EventRepository
import javax.inject.Inject

class UpdateEvent @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(id: String, request: EventCreationRequest): Event {
        require(id.isNotBlank()) { "Event ID is required" }
        require(request.name.length >= 3) { "Event name must be at least 3 characters" }
        require(request.description.isNotBlank()) { "Description is required" }
        require(request.location.length >= 3) { "Location must be at least 3 characters" }
        return eventRepository.updateEvent(id, request)
    }
}
