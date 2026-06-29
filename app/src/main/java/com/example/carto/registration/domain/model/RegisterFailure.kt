package com.example.carto.registration.domain.model

data class RegisterFailure(
    val type: RegisterFailureType,
    val developerMessage: String,
)
