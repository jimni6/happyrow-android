package com.happyrow.android.usecases.events

import com.happyrow.android.domain.model.Event
import com.happyrow.android.domain.repository.EventRepository
import javax.inject.Inject

class GetEventById @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(id: String): Event? {
        require(id.isNotBlank()) { "Event ID is required" }
        return eventRepository.getEventById(id)
    }
}
