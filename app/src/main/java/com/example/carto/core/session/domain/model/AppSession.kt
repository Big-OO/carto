package com.example.carto.core.session.domain.model

data class AppSession(
    val isLoggedIn: Boolean = false,
    val isGuest: Boolean = false,
    val customerId: String? = null,
)
