package com.happyrow.android.usecases.events

import com.happyrow.android.domain.model.Event
import com.happyrow.android.domain.repository.EventRepository
import javax.inject.Inject

class GetEventsByOrganizer @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(organizerId: String): List<Event> {
        require(organizerId.isNotBlank()) { "Organizer ID is required" }
        return eventRepository.getEventsByOrganizer(organizerId)
    }
}
