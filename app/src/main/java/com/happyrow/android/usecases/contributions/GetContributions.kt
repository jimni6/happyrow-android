package com.happyrow.android.usecases.contributions

import com.happyrow.android.domain.model.Contribution
import com.happyrow.android.domain.repository.ContributionRepository
import javax.inject.Inject

class GetContributions @Inject constructor(
    private val contributionRepository: ContributionRepository
) {
    suspend operator fun invoke(eventId: String, resourceId: String): List<Contribution> {
        require(eventId.isNotBlank()) { "Event ID is required" }
        require(resourceId.isNotBlank()) { "Resource ID is required" }
        return contributionRepository.getContributionsByResource(eventId, resourceId)
    }
}
