package com.example.carto.registration.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.carto.registration.presentation.state.RegisterFormUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegisterViewModel : ViewModel(), RegisterInteractionListener {
    private val _state: MutableStateFlow<RegisterFormUiState> =
        MutableStateFlow(RegisterFormUiState())
    val state = _state.asStateFlow()


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
