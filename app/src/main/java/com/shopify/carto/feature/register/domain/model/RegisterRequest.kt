package com.shopify.carto.feature.register.domain.model

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String,
)
