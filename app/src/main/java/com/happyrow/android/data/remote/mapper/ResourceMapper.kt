package com.happyrow.android.data.remote.mapper

import com.happyrow.android.data.remote.dto.ContributorApiResponse
import com.happyrow.android.data.remote.dto.ResourceApiRequest
import com.happyrow.android.data.remote.dto.ResourceApiResponse
import com.happyrow.android.domain.model.Resource
import com.happyrow.android.domain.model.ResourceCategory
import com.happyrow.android.domain.model.ResourceContributor
import com.happyrow.android.domain.model.ResourceCreationRequest
import com.happyrow.android.domain.model.ResourceUpdateRequest

fun ContributorApiResponse.toDomain(): ResourceContributor = ResourceContributor(
    userId = user_id,
    quantity = quantity,
    contributedAt = contributed_at
)

fun ResourceApiResponse.toDomain(): Resource = Resource(
    id = identifier,
    eventId = event_id,
    name = name,
    category = runCatching { ResourceCategory.valueOf(category.uppercase()) }
        .getOrDefault(ResourceCategory.OTHER),
    currentQuantity = current_quantity,
    suggestedQuantity = suggested_quantity,
    contributors = contributors.map { it.toDomain() },
    createdAt = created_at,
    updatedAt = updated_at
)

fun ResourceCreationRequest.toApiRequest(): ResourceApiRequest = ResourceApiRequest(
    name = name,
    category = category.name,
    quantity = quantity,
    suggested_quantity = suggestedQuantity
)

fun ResourceUpdateRequest.toApiRequest(): ResourceApiRequest = ResourceApiRequest(
    name = name ?: "",
    category = (category ?: ResourceCategory.OTHER).name,
    quantity = quantity ?: 0,
    suggested_quantity = suggestedQuantity
)
