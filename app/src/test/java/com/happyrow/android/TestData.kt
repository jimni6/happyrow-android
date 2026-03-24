package com.happyrow.android

import com.happyrow.android.domain.model.*

object TestData {

    fun user(
        id: String = "user-1",
        email: String = "test@example.com",
        emailConfirmed: Boolean = true,
        firstname: String = "John",
        lastname: String = "Doe"
    ) = User(
        id = id, email = email, emailConfirmed = emailConfirmed,
        firstname = firstname, lastname = lastname,
        createdAt = 1700000000L, updatedAt = 1700000000L
    )

    fun authSession(user: User = user()) = AuthSession(
        user = user, accessToken = "token-abc",
        refreshToken = "refresh-abc", expiresAt = 9999999999L
    )

    fun credentials(email: String = "test@example.com", password: String = "password123") =
        UserCredentials(email, password)

    fun registration(
        email: String = "new@example.com",
        password: String = "password123",
        firstname: String = "Jane",
        lastname: String = "Doe",
        confirmPassword: String? = "password123"
    ) = UserRegistration(email, password, firstname, lastname, confirmPassword)

    fun event(
        id: String = "event-1",
        name: String = "Birthday Party",
        description: String = "A fun party",
        date: Long = 1700000000L,
        location: String = "Paris",
        type: EventType = EventType.BIRTHDAY,
        organizerId: String = "user-1"
    ) = Event(id, name, description, date, location, type, organizerId)

    fun eventCreationRequest(
        name: String = "Birthday Party",
        description: String = "A fun party",
        date: String = "2025-12-31",
        location: String = "Paris",
        type: EventType = EventType.BIRTHDAY,
        organizerId: String = "user-1"
    ) = EventCreationRequest(name, description, date, location, type, organizerId)

    fun participant(
        userEmail: String = "guest@example.com",
        userName: String? = "Guest",
        eventId: String = "event-1",
        status: ParticipantStatus = ParticipantStatus.INVITED
    ) = Participant(userEmail, userName, eventId, status, joinedAt = 1700000000L)

    fun resource(
        id: String = "resource-1",
        eventId: String = "event-1",
        name: String = "Chips",
        category: ResourceCategory = ResourceCategory.FOOD,
        currentQuantity: Int = 2,
        suggestedQuantity: Int? = 5
    ) = Resource(id, eventId, name, category, currentQuantity, suggestedQuantity, emptyList(), 1700000000L)

    fun resourceCreationRequest(
        eventId: String = "event-1",
        name: String = "Chips",
        category: ResourceCategory = ResourceCategory.FOOD,
        quantity: Int = 5
    ) = ResourceCreationRequest(eventId, name, category, quantity)

    fun contribution(
        id: String = "contrib-1",
        eventId: String = "event-1",
        resourceId: String = "resource-1",
        userId: String = "user-1",
        quantity: Int = 2
    ) = Contribution(id, eventId, resourceId, userId, quantity, createdAt = 1700000000L)
}
