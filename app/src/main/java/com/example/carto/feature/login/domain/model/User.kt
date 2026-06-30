package com.example.carto.feature.login.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String?,
    val customerId: String?,
)
