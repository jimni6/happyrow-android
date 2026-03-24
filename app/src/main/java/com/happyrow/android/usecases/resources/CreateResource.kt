package com.happyrow.android.usecases.resources

import com.happyrow.android.domain.model.Resource
import com.happyrow.android.domain.model.ResourceCreationRequest
import com.happyrow.android.domain.repository.ResourceRepository
import javax.inject.Inject

class CreateResource @Inject constructor(
    private val resourceRepository: ResourceRepository
) {
    suspend operator fun invoke(request: ResourceCreationRequest): Resource {
        require(request.eventId.isNotBlank()) { "Event ID is required" }
        require(request.name.length >= 2) { "Resource name must be at least 2 characters" }
        require(request.quantity > 0) { "Quantity must be greater than 0" }
        return resourceRepository.createResource(request)
    }
}
