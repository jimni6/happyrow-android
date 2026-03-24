package com.happyrow.android.data.repository

import com.happyrow.android.data.remote.api.ResourceApi
import com.happyrow.android.data.remote.mapper.toApiRequest
import com.happyrow.android.data.remote.mapper.toDomain
import com.happyrow.android.domain.model.Resource
import com.happyrow.android.domain.model.ResourceCreationRequest
import com.happyrow.android.domain.model.ResourceUpdateRequest
import com.happyrow.android.domain.repository.ResourceRepository
import javax.inject.Inject

class ResourceRepositoryImpl @Inject constructor(
    private val resourceApi: ResourceApi
) : ResourceRepository {

    override suspend fun createResource(data: ResourceCreationRequest): Resource {
        return resourceApi.createResource(
            eventId = data.eventId,
            request = data.toApiRequest()
        ).toDomain()
    }

    override suspend fun getResourcesByEvent(eventId: String): List<Resource> {
        return resourceApi.getResourcesByEvent(eventId).map { it.toDomain() }
    }

    override suspend fun getResourceById(id: String): Resource? {
        return try {
            resourceApi.getResourceById(id).toDomain()
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404) null else throw e
        }
    }

    override suspend fun updateResource(id: String, data: ResourceUpdateRequest): Resource {
        return resourceApi.updateResource(id, data.toApiRequest()).toDomain()
    }

    override suspend fun deleteResource(id: String) {
        resourceApi.deleteResource(id)
    }
}
