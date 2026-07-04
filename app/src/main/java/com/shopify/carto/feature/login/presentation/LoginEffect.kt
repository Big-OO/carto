package com.shopify.carto.feature.login.presentation

sealed interface LoginEffect {

    data object NavigateToHome : LoginEffect

    data object NavigateToRegister : LoginEffect

    data object NavigateToForgotPassword : LoginEffect

    data class ShowError(
        val message: String
    ) : LoginEffect
}