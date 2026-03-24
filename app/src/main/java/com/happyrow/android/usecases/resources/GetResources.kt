package com.happyrow.android.usecases.resources

import com.happyrow.android.domain.model.Resource
import com.happyrow.android.domain.repository.ResourceRepository
import javax.inject.Inject

class GetResources @Inject constructor(
    private val resourceRepository: ResourceRepository
) {
    suspend operator fun invoke(eventId: String): List<Resource> {
        require(eventId.isNotBlank()) { "Event ID is required" }
        return resourceRepository.getResourcesByEvent(eventId)
    }
}
