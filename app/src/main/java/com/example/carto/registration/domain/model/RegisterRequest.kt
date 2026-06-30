package com.example.carto.registration.domain.model

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String,
)
