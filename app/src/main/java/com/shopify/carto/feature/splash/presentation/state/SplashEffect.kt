package com.shopify.carto.feature.splash.presentation.state

sealed interface SplashEffect {
    data class Navigate(val destination: SplashDestination) : SplashEffect
}
