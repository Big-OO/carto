package com.shopify.carto.feature.forgetpassword.presentation

sealed interface ForgotPasswordEffect {
    data class ShowMessage(val message: String) : ForgotPasswordEffect
    data object NavigateBack : ForgotPasswordEffect
}
