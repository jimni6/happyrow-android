package com.happyrow.android.domain.repository

import com.happyrow.android.domain.model.Event
import com.happyrow.android.domain.model.EventCreationRequest

interface EventRepository {
    suspend fun createEvent(eventData: EventCreationRequest): Event
    suspend fun getEventById(id: String): Event?
    suspend fun getEventsByOrganizer(organizerId: String): List<Event>
    suspend fun updateEvent(id: String, eventData: EventCreationRequest): Event
    suspend fun deleteEvent(id: String, userId: String)
}
