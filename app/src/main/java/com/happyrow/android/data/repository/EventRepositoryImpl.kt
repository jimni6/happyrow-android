package com.happyrow.android.data.repository

import com.happyrow.android.data.remote.api.EventApi
import com.happyrow.android.data.remote.mapper.toApiRequest
import com.happyrow.android.data.remote.mapper.toDomain
import com.happyrow.android.domain.model.Event
import com.happyrow.android.domain.model.EventCreationRequest
import com.happyrow.android.domain.repository.EventRepository
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventApi: EventApi
) : EventRepository {

    override suspend fun createEvent(eventData: EventCreationRequest): Event {
        return eventApi.createEvent(eventData.toApiRequest()).toDomain()
    }

    override suspend fun getEventById(id: String): Event? {
        val response = eventApi.getEventById(id)
        return if (response.isSuccessful) {
            response.body()?.toDomain()
        } else if (response.code() == 404) {
            null
        } else {
            throw RuntimeException("Failed to get event: HTTP ${response.code()}")
        }
    }

    override suspend fun getEventsByOrganizer(organizerId: String): List<Event> {
        return eventApi.getAllEvents()
            .filter { it.creator == organizerId }
            .map { it.toDomain() }
    }

    override suspend fun updateEvent(id: String, eventData: EventCreationRequest): Event {
        return eventApi.updateEvent(id, eventData.toApiRequest()).toDomain()
    }

    override suspend fun deleteEvent(id: String, userId: String) {
        val response = eventApi.deleteEvent(id)
        if (!response.isSuccessful) {
            throw RuntimeException("Failed to delete event: HTTP ${response.code()}")
        }
    }
}
