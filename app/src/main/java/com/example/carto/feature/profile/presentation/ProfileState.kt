package com.example.carto.feature.profile.presentation

data class ProfileState(
    val id: String,
    val name: String,
    val email: String,
    val phone: String?,
    val ordersCount: Int,
    val totalSpent: String
)