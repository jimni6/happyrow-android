package com.happyrow.android.data.repository

import com.happyrow.android.data.remote.api.EventApi
import com.happyrow.android.data.remote.dto.EventApiRequest
import com.happyrow.android.data.remote.dto.EventApiResponse
import com.happyrow.android.domain.model.EventCreationRequest
import com.happyrow.android.domain.model.EventType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class EventRepositoryImplTest {

    private lateinit var api: EventApi
    private lateinit var repository: EventRepositoryImpl

    @Before
    fun setup() {
        api = mockk()
        repository = EventRepositoryImpl(api)
    }

    private fun apiResponse(id: String = "event-1") = EventApiResponse(
        identifier = id, name = "Party", description = "Fun",
        event_date = 1700000000L, location = "Paris", type = "BIRTHDAY",
        creator = "org-1"
    )

    @Test
    fun `createEvent returns mapped event`() = runTest {
        coEvery { api.createEvent(any()) } returns apiResponse()
        val request = EventCreationRequest("Party", "Fun", "2025-12-31", "Paris", EventType.BIRTHDAY, "org-1")
        val event = repository.createEvent(request)
        assertEquals("event-1", event.id)
        assertEquals("Party", event.name)
        assertEquals(EventType.BIRTHDAY, event.type)
    }

    @Test
    fun `getEventById returns event on success`() = runTest {
        coEvery { api.getEventById("event-1") } returns Response.success(apiResponse())
        val event = repository.getEventById("event-1")
        assertNotNull(event)
        assertEquals("event-1", event!!.id)
    }

    @Test
    fun `getEventById returns null on 404`() = runTest {
        coEvery { api.getEventById("missing") } returns Response.error(404, "".toResponseBody())
        val event = repository.getEventById("missing")
        assertNull(event)
    }

    @Test
    fun `getEventById throws on other HTTP errors`() = runTest {
        coEvery { api.getEventById("x") } returns Response.error(500, "".toResponseBody())
        try {
            repository.getEventById("x")
            fail("Should throw")
        } catch (e: RuntimeException) {
            assertTrue(e.message!!.contains("500"))
        }
    }

    @Test
    fun `getEventsByOrganizer filters by creator`() = runTest {
        coEvery { api.getAllEvents() } returns listOf(
            apiResponse("e-1"),
            EventApiResponse("e-2", "Other", "X", 0L, "X", "PARTY", creator = "other-user")
        )
        val events = repository.getEventsByOrganizer("org-1")
        assertEquals(1, events.size)
        assertEquals("e-1", events[0].id)
    }

    @Test
    fun `deleteEvent succeeds on 200`() = runTest {
        coEvery { api.deleteEvent("event-1") } returns Response.success(Unit)
        repository.deleteEvent("event-1", "user-1")
    }

    @Test
    fun `deleteEvent throws on error`() = runTest {
        coEvery { api.deleteEvent("event-1") } returns Response.error(403, "".toResponseBody())
        try {
            repository.deleteEvent("event-1", "user-1")
            fail("Should throw")
        } catch (e: RuntimeException) {
            assertTrue(e.message!!.contains("403"))
        }
    }
}
