package com.happyrow.android.usecases.resources

import com.happyrow.android.domain.repository.ResourceRepository
import javax.inject.Inject

class DeleteResource @Inject constructor(
    private val resourceRepository: ResourceRepository
) {
    suspend operator fun invoke(id: String) {
        require(id.isNotBlank()) { "Resource ID is required" }
        resourceRepository.deleteResource(id)
    }
}
