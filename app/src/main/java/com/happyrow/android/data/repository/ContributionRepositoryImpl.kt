package com.happyrow.android.data.repository

import com.happyrow.android.data.remote.api.ContributionApi
import com.happyrow.android.data.remote.mapper.toApiRequest
import com.happyrow.android.data.remote.mapper.toDomain
import com.happyrow.android.domain.model.Contribution
import com.happyrow.android.domain.model.ContributionCreationRequest
import com.happyrow.android.domain.model.ContributionUpdateRequest
import com.happyrow.android.domain.repository.ContributionRepository
import javax.inject.Inject

class ContributionRepositoryImpl @Inject constructor(
    private val contributionApi: ContributionApi
) : ContributionRepository {

    override suspend fun getContributionsByResource(
        eventId: String,
        resourceId: String
    ): List<Contribution> {
        return contributionApi.getContributionsByResource(eventId, resourceId)
            .map { it.toDomain(eventId) }
    }

    override suspend fun createContribution(data: ContributionCreationRequest): Contribution {
        return contributionApi.createContribution(
            eventId = data.eventId,
            resourceId = data.resourceId,
            request = data.toApiRequest()
        ).toDomain(data.eventId)
    }

    override suspend fun updateContribution(
        eventId: String,
        resourceId: String,
        data: ContributionUpdateRequest
    ): Contribution {
        return contributionApi.createContribution(
            eventId = eventId,
            resourceId = resourceId,
            request = com.happyrow.android.data.remote.dto.ContributionApiRequest(
                quantity = data.quantity ?: 0
            )
        ).toDomain(eventId)
    }

    override suspend fun deleteContribution(eventId: String, resourceId: String) {
        contributionApi.deleteContribution(eventId, resourceId)
    }
}
