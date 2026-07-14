package com.shopify.carto.navigation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopify.carto.core.session.domain.model.AppSession
import com.shopify.carto.core.session.domain.usecase.ClearAppSessionUseCase
import com.shopify.carto.core.session.domain.usecase.FinishOnBoardingUseCase
import com.shopify.carto.core.session.domain.usecase.ObserveAppSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSessionViewModel @Inject constructor(
    observeAppSessionUseCase: ObserveAppSessionUseCase,
    private val clearAppSessionUseCase: ClearAppSessionUseCase,
    private val finishOnBoardingUseCase: FinishOnBoardingUseCase,
) : ViewModel() {

    val session: StateFlow<AppSession?> = observeAppSessionUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    fun completeOnBoarding(onCompleted: () -> Unit = {}) {
        viewModelScope.launch {
            finishOnBoardingUseCase()
            onCompleted()
        }
    }

    fun clearSession() {
        viewModelScope.launch {
            clearAppSessionUseCase()
        }
    }
}