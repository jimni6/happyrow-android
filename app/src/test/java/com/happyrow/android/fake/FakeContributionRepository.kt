package com.happyrow.android.fake

import com.happyrow.android.TestData
import com.happyrow.android.domain.model.Contribution
import com.happyrow.android.domain.model.ContributionCreationRequest
import com.happyrow.android.domain.model.ContributionUpdateRequest
import com.happyrow.android.domain.repository.ContributionRepository

class FakeContributionRepository : ContributionRepository {

    val contributions = mutableListOf<Contribution>()
    var shouldThrow: Exception? = null
    private var idCounter = 1

    override suspend fun getContributionsByResource(eventId: String, resourceId: String): List<Contribution> {
        shouldThrow?.let { throw it }
        return contributions.filter { it.eventId == eventId && it.resourceId == resourceId }
    }

    override suspend fun createContribution(data: ContributionCreationRequest): Contribution {
        shouldThrow?.let { throw it }
        val c = TestData.contribution(id = "contrib-${idCounter++}", eventId = data.eventId, resourceId = data.resourceId, userId = data.userId, quantity = data.quantity)
        contributions.add(c)
        return c
    }

    override suspend fun updateContribution(eventId: String, resourceId: String, data: ContributionUpdateRequest): Contribution {
        shouldThrow?.let { throw it }
        val idx = contributions.indexOfFirst { it.eventId == eventId && it.resourceId == resourceId }
        if (idx == -1) throw NoSuchElementException("Contribution not found")
        val updated = contributions[idx].copy(quantity = data.quantity ?: contributions[idx].quantity)
        contributions[idx] = updated
        return updated
    }

    override suspend fun deleteContribution(eventId: String, resourceId: String) {
        shouldThrow?.let { throw it }
        contributions.removeAll { it.eventId == eventId && it.resourceId == resourceId }
    }
}
