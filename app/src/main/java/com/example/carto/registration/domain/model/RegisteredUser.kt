package com.example.carto.registration.domain.model

data class RegisteredUser(
    val firebaseUid: String,
    val shopifyCustomerId: Long,
)
