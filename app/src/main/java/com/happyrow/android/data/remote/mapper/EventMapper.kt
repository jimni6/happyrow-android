package com.happyrow.android.data.remote.mapper

import com.happyrow.android.data.remote.dto.EventApiRequest
import com.happyrow.android.data.remote.dto.EventApiResponse
import com.happyrow.android.domain.model.Event
import com.happyrow.android.domain.model.EventCreationRequest
import com.happyrow.android.domain.model.EventType

fun EventApiResponse.toDomain(): Event = Event(
    id = identifier,
    name = name,
    description = description,
    date = event_date,
    location = location,
    type = runCatching { EventType.valueOf(type.uppercase()) }.getOrDefault(EventType.PARTY),
    organizerId = creator ?: ""
)

fun EventCreationRequest.toApiRequest(): EventApiRequest = EventApiRequest(
    name = name,
    description = description,
    event_date = date,
    location = location,
    type = type.name
)
