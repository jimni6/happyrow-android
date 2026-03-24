package com.happyrow.android.data.remote.api

import com.happyrow.android.data.remote.dto.ResourceApiRequest
import com.happyrow.android.data.remote.dto.ResourceApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ResourceApi {

    @POST("events/{eventId}/resources")
    suspend fun createResource(
        @Path("eventId") eventId: String,
        @Body request: ResourceApiRequest
    ): ResourceApiResponse

    @GET("events/{eventId}/resources")
    suspend fun getResourcesByEvent(
        @Path("eventId") eventId: String
    ): List<ResourceApiResponse>

    @GET("resources/{id}")
    suspend fun getResourceById(@Path("id") id: String): ResourceApiResponse

    @PUT("resources/{id}")
    suspend fun updateResource(
        @Path("id") id: String,
        @Body request: ResourceApiRequest
    ): ResourceApiResponse

    @DELETE("resources/{id}")
    suspend fun deleteResource(@Path("id") id: String)
}
