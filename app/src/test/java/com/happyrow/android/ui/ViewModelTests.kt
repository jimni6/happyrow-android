package com.happyrow.android.ui

import com.happyrow.android.TestData
import com.happyrow.android.TestDispatcherRule
import com.happyrow.android.domain.model.ParticipantCreationRequest
import com.happyrow.android.domain.model.ParticipantStatus
import com.happyrow.android.fake.*
import com.happyrow.android.ui.events.*
import com.happyrow.android.usecases.events.*
import com.happyrow.android.usecases.participants.*
import com.happyrow.android.usecases.resources.*
import com.happyrow.android.usecases.contributions.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EventsViewModelTest {

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private lateinit var repo: FakeEventRepository
    private lateinit var viewModel: EventsViewModel

    @Before
    fun setup() {
        repo = FakeEventRepository()
        viewModel = EventsViewModel(
            getEventsByOrganizer = GetEventsByOrganizer(repo),
            createEvent = CreateEvent(repo),
            deleteEvent = DeleteEvent(repo)
        )
    }

    @Test
    fun `loadEvents returns Success with events`() = runTest {
        repo.events.add(TestData.event(organizerId = "user-1"))
        viewModel.loadEvents("user-1")
        val state = viewModel.eventsState.value
        assertTrue(state is EventsUiState.Success)
        assertEquals(1, (state as EventsUiState.Success).events.size)
    }

    @Test
    fun `loadEvents returns Success with empty list`() = runTest {
        viewModel.loadEvents("user-1")
        val state = viewModel.eventsState.value
        assertTrue(state is EventsUiState.Success)
        assertTrue((state as EventsUiState.Success).events.isEmpty())
    }

    @Test
    fun `loadEvents on error returns Error state`() = runTest {
        repo.shouldThrow = RuntimeException("Network")
        viewModel.loadEvents("user-1")
        assertTrue(viewModel.eventsState.value is EventsUiState.Error)
    }

    @Test
    fun `createNewEvent sets success`() = runTest {
        viewModel.createNewEvent(TestData.eventCreationRequest())
        assertTrue(viewModel.createEventState.value.success)
    }

    @Test
    fun `removeEvent removes from list`() = runTest {
        repo.events.add(TestData.event(id = "event-1", organizerId = "user-1"))
        viewModel.loadEvents("user-1")
        viewModel.removeEvent("event-1", "user-1")
        val state = viewModel.eventsState.value as EventsUiState.Success
        assertTrue(state.events.isEmpty())
    }
}

class ParticipantsViewModelTest {

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private lateinit var repo: FakeParticipantRepository
    private lateinit var viewModel: ParticipantsViewModel

    @Before
    fun setup() {
        repo = FakeParticipantRepository()
        viewModel = ParticipantsViewModel(
            getParticipants = GetParticipants(repo),
            addParticipant = AddParticipant(repo),
            updateParticipantStatus = UpdateParticipantStatus(repo),
            removeParticipant = RemoveParticipant(repo)
        )
    }

    @Test
    fun `loadParticipants returns Success`() = runTest {
        repo.participants.add(TestData.participant(eventId = "event-1"))
        viewModel.loadParticipants("event-1")
        val state = viewModel.uiState.value
        assertTrue(state is ParticipantsUiState.Success)
        assertEquals(1, (state as ParticipantsUiState.Success).participants.size)
    }

    @Test
    fun `addNewParticipant refreshes list`() = runTest {
        viewModel.addNewParticipant("event-1", "a@b.com", ParticipantStatus.INVITED)
        val state = viewModel.uiState.value
        assertTrue(state is ParticipantsUiState.Success)
    }
}

class ResourcesViewModelTest {

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private lateinit var repo: FakeResourceRepository
    private lateinit var viewModel: ResourcesViewModel

    @Before
    fun setup() {
        repo = FakeResourceRepository()
        viewModel = ResourcesViewModel(
            getResources = GetResources(repo),
            createResource = CreateResource(repo),
            updateResource = UpdateResource(repo),
            deleteResource = DeleteResource(repo)
        )
    }

    @Test
    fun `loadResources returns Success`() = runTest {
        repo.resources.add(TestData.resource(eventId = "event-1"))
        viewModel.loadResources("event-1")
        assertTrue(viewModel.uiState.value is ResourcesUiState.Success)
    }

    @Test
    fun `removeResource refreshes list`() = runTest {
        repo.resources.add(TestData.resource(id = "r-1", eventId = "event-1"))
        viewModel.loadResources("event-1")
        viewModel.removeResource("r-1")
        val state = viewModel.uiState.value as ResourcesUiState.Success
        assertTrue(state.resources.isEmpty())
    }
}

class ContributionsViewModelTest {

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private lateinit var repo: FakeContributionRepository
    private lateinit var viewModel: ContributionsViewModel

    @Before
    fun setup() {
        repo = FakeContributionRepository()
        viewModel = ContributionsViewModel(
            getContributions = GetContributions(repo),
            addContribution = AddContribution(repo),
            updateContribution = UpdateContribution(repo),
            deleteContribution = DeleteContribution(repo)
        )
    }

    @Test
    fun `loadContributions returns Success`() = runTest {
        repo.contributions.add(TestData.contribution(eventId = "e-1", resourceId = "r-1"))
        viewModel.loadContributions("e-1", "r-1")
        assertTrue(viewModel.uiState.value is ContributionsUiState.Success)
    }

    @Test
    fun `contribute calls onSuccess`() = runTest {
        var called = false
        viewModel.contribute(
            com.happyrow.android.domain.model.ContributionCreationRequest("e-1", "r-1", "u-1", 3),
            onSuccess = { called = true }
        )
        assertTrue(called)
    }
}
