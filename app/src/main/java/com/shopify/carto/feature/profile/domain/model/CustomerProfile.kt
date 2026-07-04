package com.shopify.carto.feature.profile.domain.model

data class CustomerProfile(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String?,
    val ordersCount: Int,
    val totalSpent: String,
)
