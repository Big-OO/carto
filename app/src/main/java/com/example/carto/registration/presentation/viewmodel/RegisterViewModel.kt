package com.example.carto.registration.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.carto.registration.domain.usecases.ValidateEmailUseCase
import com.example.carto.registration.domain.usecases.ValidateFullNameUseCase
import com.example.carto.registration.domain.usecases.ValidatePasswordUseCase
import com.example.carto.registration.presentation.state.RegisterFormUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validateFullNameUseCase: ValidateFullNameUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase
) : ViewModel(), RegisterInteractionListener {
    private val _state = MutableStateFlow(RegisterFormUiState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<RegisterSideEffects>()
    val effects = _effects.asSharedFlow()

    override fun onEmailValueChanged(newValue: String) {
        _state.update {
            it.copy(
                email = it.email.copy(value = newValue)
            )
        }
    }

    override fun onPasswordValueChanged(newValue: String) {
        _state.update {
            it.copy(
                password = it.password.copy(value = newValue)
            )
        }
    }

    override fun onFullNameValueChanged(newValue: String) {
        _state.update {
            it.copy(
                fullName = it.fullName.copy(value = newValue)
            )
        }
    }

    override fun onRegister() {
        //TODO: Send Effect to register
    }

    override fun onNavigateToLogin() {
        //TODO: Send Effect to navigate to login
    }
}
