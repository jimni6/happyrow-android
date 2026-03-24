package com.happyrow.android.fake

import com.happyrow.android.TestData
import com.happyrow.android.domain.model.Event
import com.happyrow.android.domain.model.EventCreationRequest
import com.happyrow.android.domain.repository.EventRepository

class FakeEventRepository : EventRepository {

    val events = mutableListOf<Event>()
    var shouldThrow: Exception? = null
    private var idCounter = 1

    override suspend fun createEvent(eventData: EventCreationRequest): Event {
        shouldThrow?.let { throw it }
        val event = TestData.event(
            id = "event-${idCounter++}",
            name = eventData.name,
            description = eventData.description,
            location = eventData.location,
            type = eventData.type,
            organizerId = eventData.organizerId
        )
        events.add(event)
        return event
    }

    override suspend fun getEventById(id: String): Event? {
        shouldThrow?.let { throw it }
        return events.find { it.id == id }
    }

    override suspend fun getEventsByOrganizer(organizerId: String): List<Event> {
        shouldThrow?.let { throw it }
        return events.filter { it.organizerId == organizerId }
    }

    override suspend fun updateEvent(id: String, eventData: EventCreationRequest): Event {
        shouldThrow?.let { throw it }
        val idx = events.indexOfFirst { it.id == id }
        if (idx == -1) throw NoSuchElementException("Event not found")
        val updated = events[idx].copy(name = eventData.name, description = eventData.description, location = eventData.location, type = eventData.type)
        events[idx] = updated
        return updated
    }

    override suspend fun deleteEvent(id: String, userId: String) {
        shouldThrow?.let { throw it }
        events.removeAll { it.id == id }
    }
}
