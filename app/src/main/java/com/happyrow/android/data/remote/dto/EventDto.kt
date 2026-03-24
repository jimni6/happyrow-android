package com.happyrow.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventApiRequest(
    val name: String,
    val description: String,
    val event_date: String,
    val location: String,
    val type: String
)

@Serializable
data class EventApiResponse(
    val identifier: String,
    val name: String,
    val description: String,
    val event_date: Long,
    val location: String,
    val type: String,
    val creator: String? = null,
    val creation_date: Long? = null,
    val update_date: Long? = null,
    val members: List<String>? = null
)
