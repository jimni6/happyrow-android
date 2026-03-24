package com.happyrow.android.fake

import com.happyrow.android.TestData
import com.happyrow.android.domain.model.Resource
import com.happyrow.android.domain.model.ResourceCreationRequest
import com.happyrow.android.domain.model.ResourceUpdateRequest
import com.happyrow.android.domain.repository.ResourceRepository

class FakeResourceRepository : ResourceRepository {

    val resources = mutableListOf<Resource>()
    var shouldThrow: Exception? = null
    private var idCounter = 1

    override suspend fun createResource(data: ResourceCreationRequest): Resource {
        shouldThrow?.let { throw it }
        val r = TestData.resource(id = "resource-${idCounter++}", eventId = data.eventId, name = data.name, category = data.category, suggestedQuantity = data.suggestedQuantity)
        resources.add(r)
        return r
    }

    override suspend fun getResourcesByEvent(eventId: String): List<Resource> {
        shouldThrow?.let { throw it }
        return resources.filter { it.eventId == eventId }
    }

    override suspend fun getResourceById(id: String): Resource? {
        shouldThrow?.let { throw it }
        return resources.find { it.id == id }
    }

    override suspend fun updateResource(id: String, data: ResourceUpdateRequest): Resource {
        shouldThrow?.let { throw it }
        val idx = resources.indexOfFirst { it.id == id }
        if (idx == -1) throw NoSuchElementException("Resource not found")
        val current = resources[idx]
        val updated = current.copy(
            name = data.name ?: current.name,
            category = data.category ?: current.category,
            currentQuantity = data.quantity ?: current.currentQuantity,
            suggestedQuantity = data.suggestedQuantity ?: current.suggestedQuantity
        )
        resources[idx] = updated
        return updated
    }

    override suspend fun deleteResource(id: String) {
        shouldThrow?.let { throw it }
        resources.removeAll { it.id == id }
    }
}
