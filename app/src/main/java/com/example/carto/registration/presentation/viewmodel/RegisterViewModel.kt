package com.example.carto.registration.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.carto.registration.presentation.state.RegisterFormUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


@HiltViewModel
class RegisterViewModel : ViewModel(), RegisterInteractionListener {
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
                email = it.password.copy(value = newValue)
            )
        }
    }

    override fun onFullNameValueChanged(newValue: String) {
        _state.update {
            it.copy(
                email = it.fullName.copy(value = newValue)
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
