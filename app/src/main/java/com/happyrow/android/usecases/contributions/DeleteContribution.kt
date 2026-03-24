package com.happyrow.android.usecases.contributions

import com.happyrow.android.domain.repository.ContributionRepository
import javax.inject.Inject

class DeleteContribution @Inject constructor(
    private val contributionRepository: ContributionRepository
) {
    suspend operator fun invoke(eventId: String, resourceId: String) {
        require(eventId.isNotBlank()) { "Event ID is required" }
        require(resourceId.isNotBlank()) { "Resource ID is required" }
        contributionRepository.deleteContribution(eventId, resourceId)
    }
}
