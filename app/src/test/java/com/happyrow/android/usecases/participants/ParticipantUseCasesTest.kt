package com.happyrow.android.usecases.participants

import com.happyrow.android.TestData
import com.happyrow.android.domain.model.ParticipantCreationRequest
import com.happyrow.android.domain.model.ParticipantStatus
import com.happyrow.android.domain.model.ParticipantUpdateRequest
import com.happyrow.android.fake.FakeParticipantRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ParticipantUseCasesTest {

    private lateinit var repo: FakeParticipantRepository
    private lateinit var addParticipant: AddParticipant
    private lateinit var getParticipants: GetParticipants
    private lateinit var updateStatus: UpdateParticipantStatus
    private lateinit var removeParticipant: RemoveParticipant

    @Before
    fun setup() {
        repo = FakeParticipantRepository()
        addParticipant = AddParticipant(repo)
        getParticipants = GetParticipants(repo)
        updateStatus = UpdateParticipantStatus(repo)
        removeParticipant = RemoveParticipant(repo)
    }

    @Test
    fun `addParticipant with valid data succeeds`() = runTest {
        val p = addParticipant(ParticipantCreationRequest("event-1", "guest@example.com", ParticipantStatus.INVITED))
        assertEquals("guest@example.com", p.userEmail)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `addParticipant with empty eventId throws`() = runTest {
        addParticipant(ParticipantCreationRequest("", "guest@example.com", ParticipantStatus.INVITED))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `addParticipant with invalid email throws`() = runTest {
        addParticipant(ParticipantCreationRequest("event-1", "invalid", ParticipantStatus.INVITED))
    }

    @Test
    fun `getParticipants returns list`() = runTest {
        repo.participants.add(TestData.participant(eventId = "event-1"))
        val result = getParticipants("event-1")
        assertEquals(1, result.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `getParticipants with empty eventId throws`() = runTest {
        getParticipants("")
    }

    @Test
    fun `updateStatus succeeds`() = runTest {
        repo.participants.add(TestData.participant(eventId = "event-1", userEmail = "a@b.com"))
        val updated = updateStatus("event-1", "a@b.com", ParticipantUpdateRequest(ParticipantStatus.CONFIRMED))
        assertEquals(ParticipantStatus.CONFIRMED, updated.status)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `updateStatus with empty eventId throws`() = runTest {
        updateStatus("", "a@b.com", ParticipantUpdateRequest(ParticipantStatus.CONFIRMED))
    }

    @Test
    fun `removeParticipant succeeds`() = runTest {
        repo.participants.add(TestData.participant(eventId = "event-1", userEmail = "a@b.com"))
        removeParticipant("event-1", "a@b.com")
        assertTrue(repo.participants.isEmpty())
    }

    @Test(expected = IllegalArgumentException::class)
    fun `removeParticipant with empty eventId throws`() = runTest {
        removeParticipant("", "a@b.com")
    }
}
