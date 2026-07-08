package com.shopify.carto.feature.forgetpassword.presentation

sealed interface ForgotPasswordEvent {
    data class EmailChanged(val email: String) : ForgotPasswordEvent
    data object SubmitClicked : ForgotPasswordEvent
}
