package com.happyrow.android.usecases.resources

import com.happyrow.android.TestData
import com.happyrow.android.domain.model.ResourceUpdateRequest
import com.happyrow.android.fake.FakeResourceRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ResourceUseCasesTest {

    private lateinit var repo: FakeResourceRepository
    private lateinit var createResource: CreateResource
    private lateinit var getResources: GetResources
    private lateinit var updateResource: UpdateResource
    private lateinit var deleteResource: DeleteResource

    @Before
    fun setup() {
        repo = FakeResourceRepository()
        createResource = CreateResource(repo)
        getResources = GetResources(repo)
        updateResource = UpdateResource(repo)
        deleteResource = DeleteResource(repo)
    }

    @Test
    fun `createResource with valid data succeeds`() = runTest {
        val r = createResource(TestData.resourceCreationRequest())
        assertEquals("Chips", r.name)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `createResource with empty eventId throws`() = runTest {
        createResource(TestData.resourceCreationRequest(eventId = ""))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `createResource with short name throws`() = runTest {
        createResource(TestData.resourceCreationRequest(name = "A"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `createResource with zero quantity throws`() = runTest {
        createResource(TestData.resourceCreationRequest(quantity = 0))
    }

    @Test
    fun `getResources returns list`() = runTest {
        repo.resources.add(TestData.resource(eventId = "event-1"))
        val result = getResources("event-1")
        assertEquals(1, result.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `getResources with empty eventId throws`() = runTest {
        getResources("")
    }

    @Test
    fun `updateResource succeeds`() = runTest {
        repo.resources.add(TestData.resource(id = "r-1"))
        val updated = updateResource("r-1", ResourceUpdateRequest(name = "Updated"))
        assertEquals("Updated", updated.name)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `updateResource with empty id throws`() = runTest {
        updateResource("", ResourceUpdateRequest(name = "X"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `updateResource with no fields throws`() = runTest {
        updateResource("r-1", ResourceUpdateRequest())
    }

    @Test
    fun `deleteResource succeeds`() = runTest {
        repo.resources.add(TestData.resource(id = "r-1"))
        deleteResource("r-1")
        assertTrue(repo.resources.isEmpty())
    }

    @Test(expected = IllegalArgumentException::class)
    fun `deleteResource with empty id throws`() = runTest {
        deleteResource("")
    }
}
