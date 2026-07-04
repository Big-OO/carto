package com.example.carto.feature.splash.presentation.state

sealed interface SplashEffect {
    data object NavigateToOnBoarding : SplashEffect
    data object NavigateToLogin : SplashEffect
    data object NavigateToHome : SplashEffect
}
