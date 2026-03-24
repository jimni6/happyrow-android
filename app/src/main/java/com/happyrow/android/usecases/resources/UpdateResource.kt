package com.happyrow.android.usecases.resources

import com.happyrow.android.domain.model.Resource
import com.happyrow.android.domain.model.ResourceUpdateRequest
import com.happyrow.android.domain.repository.ResourceRepository
import javax.inject.Inject

class UpdateResource @Inject constructor(
    private val resourceRepository: ResourceRepository
) {
    suspend operator fun invoke(id: String, data: ResourceUpdateRequest): Resource {
        require(id.isNotBlank()) { "Resource ID is required" }
        require(
            data.name != null || data.category != null || data.quantity != null || data.suggestedQuantity != null
        ) { "At least one field must be modified" }
        return resourceRepository.updateResource(id, data)
    }
}
