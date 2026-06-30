package com.example.carto.feature.register.domain.model

data class RegisteredUser(
    val firebaseUid: String,
    val shopifyCustomerId: Long,
)
