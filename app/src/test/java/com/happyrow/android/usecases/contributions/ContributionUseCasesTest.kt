package com.happyrow.android.usecases.contributions

import com.happyrow.android.TestData
import com.happyrow.android.domain.model.ContributionCreationRequest
import com.happyrow.android.domain.model.ContributionUpdateRequest
import com.happyrow.android.fake.FakeContributionRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ContributionUseCasesTest {

    private lateinit var repo: FakeContributionRepository
    private lateinit var addContribution: AddContribution
    private lateinit var getContributions: GetContributions
    private lateinit var updateContribution: UpdateContribution
    private lateinit var deleteContribution: DeleteContribution

    @Before
    fun setup() {
        repo = FakeContributionRepository()
        addContribution = AddContribution(repo)
        getContributions = GetContributions(repo)
        updateContribution = UpdateContribution(repo)
        deleteContribution = DeleteContribution(repo)
    }

    @Test
    fun `addContribution with valid data succeeds`() = runTest {
        val c = addContribution(ContributionCreationRequest("event-1", "resource-1", "user-1", 3))
        assertEquals(3, c.quantity)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `addContribution with empty eventId throws`() = runTest {
        addContribution(ContributionCreationRequest("", "resource-1", "user-1", 3))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `addContribution with empty resourceId throws`() = runTest {
        addContribution(ContributionCreationRequest("event-1", "", "user-1", 3))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `addContribution with empty userId throws`() = runTest {
        addContribution(ContributionCreationRequest("event-1", "resource-1", "", 3))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `addContribution with zero quantity throws`() = runTest {
        addContribution(ContributionCreationRequest("event-1", "resource-1", "user-1", 0))
    }

    @Test
    fun `getContributions returns list`() = runTest {
        repo.contributions.add(TestData.contribution(eventId = "event-1", resourceId = "resource-1"))
        val result = getContributions("event-1", "resource-1")
        assertEquals(1, result.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `getContributions with empty eventId throws`() = runTest {
        getContributions("", "resource-1")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `getContributions with empty resourceId throws`() = runTest {
        getContributions("event-1", "")
    }

    @Test
    fun `updateContribution succeeds`() = runTest {
        repo.contributions.add(TestData.contribution(eventId = "e-1", resourceId = "r-1"))
        val updated = updateContribution("e-1", "r-1", ContributionUpdateRequest(quantity = 5))
        assertEquals(5, updated.quantity)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `updateContribution with zero quantity throws`() = runTest {
        updateContribution("e-1", "r-1", ContributionUpdateRequest(quantity = 0))
    }

    @Test
    fun `deleteContribution succeeds`() = runTest {
        repo.contributions.add(TestData.contribution(eventId = "e-1", resourceId = "r-1"))
        deleteContribution("e-1", "r-1")
        assertTrue(repo.contributions.isEmpty())
    }

    @Test(expected = IllegalArgumentException::class)
    fun `deleteContribution with empty eventId throws`() = runTest {
        deleteContribution("", "r-1")
    }
}
