package com.example.carto.feature.register.domain.model

data class RegisterFailure(
    val type: RegisterFailureType,
    val message: String,
)
