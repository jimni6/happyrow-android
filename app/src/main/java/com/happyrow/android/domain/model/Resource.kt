package com.happyrow.android.domain.model

enum class ResourceCategory { FOOD, DRINK, UTENSIL, DECORATION, OTHER }

data class ResourceContributor(
    val userId: String,
    val quantity: Int,
    val contributedAt: Long
)

data class Resource(
    val id: String,
    val eventId: String,
    val name: String,
    val category: ResourceCategory,
    val currentQuantity: Int,
    val suggestedQuantity: Int? = null,
    val contributors: List<ResourceContributor>,
    val createdAt: Long,
    val updatedAt: Long? = null
)

data class ResourceCreationRequest(
    val eventId: String,
    val name: String,
    val category: ResourceCategory,
    val quantity: Int,
    val suggestedQuantity: Int? = null
)

data class ResourceUpdateRequest(
    val name: String? = null,
    val category: ResourceCategory? = null,
    val quantity: Int? = null,
    val suggestedQuantity: Int? = null
)
