package com.happyrow.android.usecases.events

import com.happyrow.android.domain.model.Event
import com.happyrow.android.domain.model.EventCreationRequest
import com.happyrow.android.domain.repository.EventRepository
import javax.inject.Inject

class CreateEvent @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(request: EventCreationRequest): Event {
        require(request.name.length >= 3) { "Event name must be at least 3 characters" }
        require(request.description.isNotBlank()) { "Description is required" }
        require(request.location.length >= 3) { "Location must be at least 3 characters" }
        require(request.organizerId.isNotBlank()) { "Organizer ID is required" }
        return eventRepository.createEvent(request)
    }
}
