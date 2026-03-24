package com.happyrow.android.data.remote.api

import com.happyrow.android.data.remote.dto.ContributionApiRequest
import com.happyrow.android.data.remote.dto.ContributionApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ContributionApi {

    @GET("events/{eventId}/resources/{resourceId}/contributions")
    suspend fun getContributionsByResource(
        @Path("eventId") eventId: String,
        @Path("resourceId") resourceId: String
    ): List<ContributionApiResponse>

    @POST("events/{eventId}/resources/{resourceId}/contributions")
    suspend fun createContribution(
        @Path("eventId") eventId: String,
        @Path("resourceId") resourceId: String,
        @Body request: ContributionApiRequest
    ): ContributionApiResponse

    @DELETE("events/{eventId}/resources/{resourceId}/contributions")
    suspend fun deleteContribution(
        @Path("eventId") eventId: String,
        @Path("resourceId") resourceId: String
    )
}
