package com.shopify.carto.feature.profile.presentation

sealed interface ProfileEvent {
    data object LogoutClicked : ProfileEvent
    data object LoginClicked : ProfileEvent
    data object RetryClicked : ProfileEvent
    data class SaveProfileClicked(val name: String) : ProfileEvent
    data object SettingClicked : ProfileEvent
}
