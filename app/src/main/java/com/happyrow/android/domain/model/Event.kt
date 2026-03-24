package com.happyrow.android.domain.model

enum class EventType { PARTY, BIRTHDAY, DINER, SNACK }

data class Event(
    val id: String,
    val name: String,
    val description: String,
    val date: Long,
    val location: String,
    val type: EventType,
    val organizerId: String
)

data class EventCreationRequest(
    val name: String,
    val description: String,
    val date: String,
    val location: String,
    val type: EventType,
    val organizerId: String
)
