package com.happyrow.android.data.remote.api

import com.happyrow.android.data.remote.dto.ParticipantApiRequest
import com.happyrow.android.data.remote.dto.ParticipantApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ParticipantApi {

    @POST("events/{eventId}/participants")
    suspend fun addParticipant(
        @Path("eventId") eventId: String,
        @Body request: ParticipantApiRequest
    ): ParticipantApiResponse

    @GET("events/{eventId}/participants")
    suspend fun getParticipantsByEvent(
        @Path("eventId") eventId: String
    ): List<ParticipantApiResponse>

    @PUT("events/{eventId}/participants/{userEmail}")
    suspend fun updateParticipantStatus(
        @Path("eventId") eventId: String,
        @Path("userEmail") userEmail: String,
        @Body request: ParticipantApiRequest
    ): ParticipantApiResponse

    @DELETE("events/{eventId}/participants/{userEmail}")
    suspend fun removeParticipant(
        @Path("eventId") eventId: String,
        @Path("userEmail") userEmail: String
    )
}
