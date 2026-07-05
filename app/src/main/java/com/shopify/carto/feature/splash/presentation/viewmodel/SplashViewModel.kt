package com.shopify.carto.feature.splash.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopify.carto.core.session.domain.usecase.ObserveAppSessionUseCase
import com.shopify.carto.feature.splash.presentation.state.SplashDestination
import com.shopify.carto.feature.splash.presentation.state.SplashEffect
import com.shopify.carto.feature.splash.presentation.state.SplashUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val observeAppSessionUseCase: ObserveAppSessionUseCase,
) : ViewModel(), SplashInteractionListener {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    private val _effect = Channel<SplashEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var isAnimationFinished = false
    private var hasNavigated = false

    init {
        resolveStartDestination()
    }

    private fun resolveStartDestination() {
        viewModelScope.launch {
            val session = observeAppSessionUseCase().first()

            val destination = when {
                !session.isOnboardingSeen -> SplashDestination.OnBoarding
                session.isLoggedIn -> SplashDestination.Home
                else -> SplashDestination.Login
            }

            _uiState.update {
                it.copy(destination = destination)
            }

            navigateIfReady()
        }
    }

    override fun onSplashAnimationFinished() {
        isAnimationFinished = true
        navigateIfReady()
    }

    private fun navigateIfReady() {
        if (hasNavigated || !isAnimationFinished) return

        val destination = _uiState.value.destination ?: return

        hasNavigated = true

        viewModelScope.launch {
            _effect.send(SplashEffect.Navigate(destination))
        }
    }
}