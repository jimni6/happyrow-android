package com.happyrow.android.data.remote.api

import com.happyrow.android.data.remote.dto.EventApiRequest
import com.happyrow.android.data.remote.dto.EventApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface EventApi {

    @POST("events")
    suspend fun createEvent(@Body request: EventApiRequest): EventApiResponse

    @GET("events/{id}")
    suspend fun getEventById(@Path("id") id: String): Response<EventApiResponse>

    @GET("events")
    suspend fun getAllEvents(): List<EventApiResponse>

    @PUT("events/{id}")
    suspend fun updateEvent(
        @Path("id") id: String,
        @Body request: EventApiRequest
    ): EventApiResponse

    @DELETE("events/{id}")
    suspend fun deleteEvent(@Path("id") id: String): Response<Unit>
}
