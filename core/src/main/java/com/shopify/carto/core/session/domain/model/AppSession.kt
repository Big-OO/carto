package com.shopify.carto.core.session.domain.model

data class AppSession(
    val isOnboardingSeen : Boolean = false,
    val isLoggedIn: Boolean = false,
    val isGuest: Boolean = false,
    val customerId: String? = null,
)
