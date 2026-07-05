package com.shopify.carto.feature.login.presentation

sealed interface LoginEvent {

    data class EmailChanged(
        val email: String
    ) : LoginEvent

    data class PasswordChanged(
        val password: String
    ) : LoginEvent

    data object TogglePasswordVisibility : LoginEvent

    data object LoginClicked : LoginEvent

    data object ForgotPasswordClicked : LoginEvent

    data object RegisterClicked : LoginEvent

    data object GuestLoginClicked : LoginEvent
}