package com.happyrow.android.usecases.events

import com.happyrow.android.TestData
import com.happyrow.android.fake.FakeEventRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class EventUseCasesTest {

    private lateinit var repo: FakeEventRepository
    private lateinit var createEvent: CreateEvent
    private lateinit var getEventsByOrganizer: GetEventsByOrganizer
    private lateinit var getEventById: GetEventById
    private lateinit var updateEvent: UpdateEvent
    private lateinit var deleteEvent: DeleteEvent

    @Before
    fun setup() {
        repo = FakeEventRepository()
        createEvent = CreateEvent(repo)
        getEventsByOrganizer = GetEventsByOrganizer(repo)
        getEventById = GetEventById(repo)
        updateEvent = UpdateEvent(repo)
        deleteEvent = DeleteEvent(repo)
    }

    @Test
    fun `createEvent with valid data succeeds`() = runTest {
        val event = createEvent(TestData.eventCreationRequest())
        assertEquals("Birthday Party", event.name)
        assertEquals(1, repo.events.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `createEvent with short name throws`() = runTest {
        createEvent(TestData.eventCreationRequest(name = "AB"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `createEvent with empty description throws`() = runTest {
        createEvent(TestData.eventCreationRequest(description = ""))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `createEvent with short location throws`() = runTest {
        createEvent(TestData.eventCreationRequest(location = "AB"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `createEvent with empty organizerId throws`() = runTest {
        createEvent(TestData.eventCreationRequest(organizerId = ""))
    }

    @Test
    fun `getEventsByOrganizer returns filtered events`() = runTest {
        repo.events.add(TestData.event(id = "1", organizerId = "user-1"))
        repo.events.add(TestData.event(id = "2", organizerId = "user-2"))
        val events = getEventsByOrganizer("user-1")
        assertEquals(1, events.size)
        assertEquals("1", events[0].id)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `getEventsByOrganizer with empty id throws`() = runTest {
        getEventsByOrganizer("")
    }

    @Test
    fun `getEventById returns event when found`() = runTest {
        repo.events.add(TestData.event(id = "event-1"))
        val event = getEventById("event-1")
        assertNotNull(event)
    }

    @Test
    fun `getEventById returns null when not found`() = runTest {
        val event = getEventById("missing")
        assertNull(event)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `getEventById with empty id throws`() = runTest {
        getEventById("")
    }

    @Test
    fun `updateEvent with valid data succeeds`() = runTest {
        repo.events.add(TestData.event(id = "event-1"))
        val updated = updateEvent("event-1", TestData.eventCreationRequest(name = "Updated"))
        assertEquals("Updated", updated.name)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `updateEvent with empty id throws`() = runTest {
        updateEvent("", TestData.eventCreationRequest())
    }

    @Test
    fun `deleteEvent removes event`() = runTest {
        repo.events.add(TestData.event(id = "event-1"))
        deleteEvent("event-1", "user-1")
        assertTrue(repo.events.isEmpty())
    }

    @Test(expected = IllegalArgumentException::class)
    fun `deleteEvent with empty id throws`() = runTest {
        deleteEvent("", "user-1")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `deleteEvent with empty userId throws`() = runTest {
        deleteEvent("event-1", "")
    }
}
