package com.happyrow.android.usecases.contributions

import com.happyrow.android.domain.model.Contribution
import com.happyrow.android.domain.model.ContributionCreationRequest
import com.happyrow.android.domain.repository.ContributionRepository
import javax.inject.Inject

class AddContribution @Inject constructor(
    private val contributionRepository: ContributionRepository
) {
    suspend operator fun invoke(request: ContributionCreationRequest): Contribution {
        require(request.eventId.isNotBlank()) { "Event ID is required" }
        require(request.resourceId.isNotBlank()) { "Resource ID is required" }
        require(request.userId.isNotBlank()) { "User ID is required" }
        require(request.quantity > 0) { "Quantity must be greater than 0" }
        return contributionRepository.createContribution(request)
    }
}
