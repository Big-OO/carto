package com.shopify.carto.feature.profile.presentation

sealed interface ProfileEffect {
    data object NavigateToLogin : ProfileEffect
    data object NavigateToSettings : ProfileEffect
    data class ShowError(val message: String) : ProfileEffect
    data class ShowSuccess(val message: String) : ProfileEffect
}
