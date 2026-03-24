package com.happyrow.android.data.remote.mapper

import com.happyrow.android.data.remote.dto.ContributionApiRequest
import com.happyrow.android.data.remote.dto.ContributionApiResponse
import com.happyrow.android.domain.model.Contribution
import com.happyrow.android.domain.model.ContributionCreationRequest

fun ContributionApiResponse.toDomain(eventId: String): Contribution = Contribution(
    id = identifier,
    eventId = eventId,
    resourceId = resource_id,
    userId = participant_id,
    quantity = quantity,
    createdAt = created_at
)

fun ContributionCreationRequest.toApiRequest(): ContributionApiRequest = ContributionApiRequest(
    quantity = quantity
)
