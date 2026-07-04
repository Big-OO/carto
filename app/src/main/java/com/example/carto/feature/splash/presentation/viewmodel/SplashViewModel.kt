package com.example.carto.feature.splash.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carto.core.session.domain.model.AppSession
import com.example.carto.core.session.domain.usecase.ObserveAppSessionUseCase
import com.example.carto.feature.splash.presentation.state.SplashDestination
import com.example.carto.feature.splash.presentation.state.SplashEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val observeAppSessionUseCase: ObserveAppSessionUseCase,
) : ViewModel(), SplashInteractionListener {

    private val _effect = MutableSharedFlow<SplashEffect>()
    val effect = _effect.asSharedFlow()

    private var destination: SplashDestination? = null
    private var isAnimationFinished = false
    private var isNavigationSent = false

    init {
        resolveStartDestination()
    }

    override fun onSplashAnimationFinished() {
        isAnimationFinished = true
        navigateWhenReady()
    }

    private fun resolveStartDestination() {
        viewModelScope.launch {
            val session = observeAppSessionUseCase().first()
            destination = session.toSplashDestination()
            navigateWhenReady()
        }
    }

    private fun navigateWhenReady() {
        val target = destination ?: return
        if (!isAnimationFinished || isNavigationSent) return

        isNavigationSent = true
        viewModelScope.launch {
            _effect.emit(target.toEffect())
        }
    }

    private fun AppSession.toSplashDestination(): SplashDestination {
        return when {
            !isOnboardingSeen -> SplashDestination.OnBoarding
            isLoggedIn -> SplashDestination.Home
            else -> SplashDestination.Login
        }
    }

    private fun SplashDestination.toEffect(): SplashEffect {
        return when (this) {
            SplashDestination.OnBoarding -> SplashEffect.NavigateToOnBoarding
            SplashDestination.Login -> SplashEffect.NavigateToLogin
            SplashDestination.Home -> SplashEffect.NavigateToHome
        }
    }
}
