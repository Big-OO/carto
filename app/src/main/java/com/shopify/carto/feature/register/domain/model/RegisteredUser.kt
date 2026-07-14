package com.shopify.carto.feature.register.domain.model

data class RegisteredUser(
    val firebaseUid: String,
    val shopifyCustomerId: Long,
)
