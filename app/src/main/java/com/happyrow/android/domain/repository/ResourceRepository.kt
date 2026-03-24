package com.happyrow.android.domain.repository

import com.happyrow.android.domain.model.Resource
import com.happyrow.android.domain.model.ResourceCreationRequest
import com.happyrow.android.domain.model.ResourceUpdateRequest

interface ResourceRepository {
    suspend fun createResource(data: ResourceCreationRequest): Resource
    suspend fun getResourcesByEvent(eventId: String): List<Resource>
    suspend fun getResourceById(id: String): Resource?
    suspend fun updateResource(id: String, data: ResourceUpdateRequest): Resource
    suspend fun deleteResource(id: String)
}
