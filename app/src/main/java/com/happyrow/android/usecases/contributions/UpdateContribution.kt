package com.happyrow.android.usecases.contributions

import com.happyrow.android.domain.model.Contribution
import com.happyrow.android.domain.model.ContributionUpdateRequest
import com.happyrow.android.domain.repository.ContributionRepository
import javax.inject.Inject

class UpdateContribution @Inject constructor(
    private val contributionRepository: ContributionRepository
) {
    suspend operator fun invoke(
        eventId: String,
        resourceId: String,
        data: ContributionUpdateRequest
    ): Contribution {
        require(eventId.isNotBlank()) { "Event ID is required" }
        require(resourceId.isNotBlank()) { "Resource ID is required" }
        require(data.quantity != null && data.quantity > 0) { "Quantity must be greater than 0" }
        return contributionRepository.updateContribution(eventId, resourceId, data)
    }
}
