package com.example.carto.feature.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carto.feature.login.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _effect = Channel<LoginEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _state.update {
                    it.copy(
                        email = event.email
                    )
                }
                validateForm()
            }

            is LoginEvent.PasswordChanged -> {
                _state.update {
                    it.copy(
                        password = event.password
                    )
                }
                validateForm()
            }

            LoginEvent.TogglePasswordVisibility -> {
                _state.update {
                    it.copy(
                        isPasswordVisible =
                            !it.isPasswordVisible
                    )
                }
            }

            LoginEvent.LoginClicked -> {
                login()
            }

            LoginEvent.ForgotPasswordClicked -> {
                sendEffect(
                    LoginEffect.NavigateToForgotPassword
                )
            }

            LoginEvent.RegisterClicked -> {
                sendEffect(
                    LoginEffect.NavigateToRegister
                )
            }
        }
    }

    private fun validateForm() {
        val current = _state.value

        val isEmailValid =
            current.email.isNotBlank() &&
                    android.util.Patterns.EMAIL_ADDRESS
                        .matcher(current.email)
                        .matches()

        val isValid = isEmailValid && current.password.length >= 6

        _state.update {
            it.copy(
                isEmailValid = isEmailValid,
                isLoginEnabled = isValid
            )
        }
    }

    private fun login() {
        viewModelScope.launch {

            _state.update {
                it.copy(isLoading = true)
            }

            loginUseCase(
                _state.value.email,
                _state.value.password
            )
                .onSuccess {
                    _state.update {
                        it.copy(isLoading = false)
                    }

                    sendEffect(
                        LoginEffect.NavigateToHome
                    )
                }
                .onFailure { exception ->
                    _state.update {
                        it.copy(isLoading = false)
                    }

                    sendEffect(
                        LoginEffect.ShowError(
                            exception.message
                                ?: "Something went wrong"
                        )
                    )
                }
        }
    }

    private fun sendEffect(
        effect: LoginEffect
    ) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}