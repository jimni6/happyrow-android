package com.happyrow.android.domain.repository

import com.happyrow.android.domain.model.Contribution
import com.happyrow.android.domain.model.ContributionCreationRequest
import com.happyrow.android.domain.model.ContributionUpdateRequest

interface ContributionRepository {
    suspend fun getContributionsByResource(eventId: String, resourceId: String): List<Contribution>
    suspend fun createContribution(data: ContributionCreationRequest): Contribution
    suspend fun updateContribution(
        eventId: String,
        resourceId: String,
        data: ContributionUpdateRequest
    ): Contribution
    suspend fun deleteContribution(eventId: String, resourceId: String)
}
