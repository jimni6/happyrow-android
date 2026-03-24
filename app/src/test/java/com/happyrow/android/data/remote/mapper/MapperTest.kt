package com.happyrow.android.data.remote.mapper

import com.happyrow.android.data.remote.dto.*
import com.happyrow.android.domain.model.*
import org.junit.Assert.*
import org.junit.Test

class MapperTest {

    // EventMapper tests
    @Test
    fun `EventApiResponse toDomain maps fields correctly`() {
        val dto = EventApiResponse(
            identifier = "id-1", name = "Party", description = "Fun",
            event_date = 1700000000L, location = "Paris", type = "BIRTHDAY",
            creator = "org-1", creation_date = 1700000000L
        )
        val domain = dto.toDomain()
        assertEquals("id-1", domain.id)
        assertEquals("Party", domain.name)
        assertEquals(1700000000L, domain.date)
        assertEquals("Paris", domain.location)
        assertEquals(EventType.BIRTHDAY, domain.type)
        assertEquals("org-1", domain.organizerId)
    }

    @Test
    fun `EventApiResponse toDomain handles unknown type`() {
        val dto = EventApiResponse(
            identifier = "id-1", name = "P", description = "D",
            event_date = 0L, location = "L", type = "UNKNOWN"
        )
        assertEquals(EventType.PARTY, dto.toDomain().type)
    }

    @Test
    fun `EventCreationRequest toApiRequest maps correctly`() {
        val request = EventCreationRequest("Party", "Fun", "2025-12-31", "Paris", EventType.DINER, "org-1")
        val api = request.toApiRequest()
        assertEquals("Party", api.name)
        assertEquals("2025-12-31", api.event_date)
        assertEquals("DINER", api.type)
    }

    // ParticipantMapper tests
    @Test
    fun `ParticipantApiResponse toDomain maps fields correctly`() {
        val dto = ParticipantApiResponse(
            user_email = "a@b.com", user_name = "Alice",
            event_id = "e-1", status = "CONFIRMED",
            joined_at = 1700000000L, updated_at = 1700000001L
        )
        val domain = dto.toDomain()
        assertEquals("a@b.com", domain.userEmail)
        assertEquals("Alice", domain.userName)
        assertEquals("e-1", domain.eventId)
        assertEquals(ParticipantStatus.CONFIRMED, domain.status)
        assertEquals(1700000000L, domain.joinedAt)
    }

    @Test
    fun `ParticipantCreationRequest toApiRequest maps correctly`() {
        val request = ParticipantCreationRequest("e-1", "a@b.com", ParticipantStatus.INVITED)
        val api = request.toApiRequest()
        assertEquals("a@b.com", api.user_email)
        assertEquals("INVITED", api.status)
    }

    // ResourceMapper tests
    @Test
    fun `ResourceApiResponse toDomain maps fields correctly`() {
        val dto = ResourceApiResponse(
            identifier = "r-1", event_id = "e-1", name = "Chips",
            category = "FOOD", current_quantity = 3, suggested_quantity = 10,
            contributors = listOf(ContributorApiResponse("u-1", 2, 1700000000L)),
            created_at = 1700000000L
        )
        val domain = dto.toDomain()
        assertEquals("r-1", domain.id)
        assertEquals("e-1", domain.eventId)
        assertEquals(ResourceCategory.FOOD, domain.category)
        assertEquals(3, domain.currentQuantity)
        assertEquals(10, domain.suggestedQuantity)
        assertEquals(1, domain.contributors.size)
        assertEquals("u-1", domain.contributors[0].userId)
    }

    @Test
    fun `ResourceCreationRequest toApiRequest maps correctly`() {
        val request = ResourceCreationRequest("e-1", "Drinks", ResourceCategory.DRINK, 5, 10)
        val api = request.toApiRequest()
        assertEquals("Drinks", api.name)
        assertEquals("DRINK", api.category)
        assertEquals(5, api.quantity)
        assertEquals(10, api.suggested_quantity)
    }

    // ContributionMapper tests
    @Test
    fun `ContributionApiResponse toDomain maps fields correctly`() {
        val dto = ContributionApiResponse(
            identifier = "c-1", participant_id = "user-1",
            resource_id = "r-1", quantity = 3, created_at = 1700000000L
        )
        val domain = dto.toDomain("event-1")
        assertEquals("c-1", domain.id)
        assertEquals("event-1", domain.eventId)
        assertEquals("r-1", domain.resourceId)
        assertEquals("user-1", domain.userId)
        assertEquals(3, domain.quantity)
    }

    @Test
    fun `ContributionCreationRequest toApiRequest maps correctly`() {
        val request = ContributionCreationRequest("e-1", "r-1", "u-1", 5)
        val api = request.toApiRequest()
        assertEquals(5, api.quantity)
    }
}
