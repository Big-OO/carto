package com.shopify.carto.feature.login.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopify.carto.core.session.domain.usecase.SaveAuthenticatedSessionUseCase
import com.shopify.carto.core.session.domain.usecase.SaveGuestSessionUseCase
import com.shopify.carto.feature.login.domain.usecase.LoginUseCase
import com.shopify.carto.feature.login.domain.usecase.LoginWithGoogleUseCase
import com.shopify.carto.feature.shopping_cart.domain.usecase.ClearCartUseCase
import com.shopify.carto.feature.shopping_cart.domain.usecase.LinkCartToUserUseCase
import com.shopify.carto.feature.shopping_cart.domain.usecase.RefreshCartUseCase
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
    private val loginUseCase: LoginUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val saveAuthenticatedSessionUseCase: SaveAuthenticatedSessionUseCase,
    private val saveGuestSessionUseCase: SaveGuestSessionUseCase,
    private val linkCartToUserUseCase: LinkCartToUserUseCase,
    private val refreshCartUseCase: RefreshCartUseCase,
    private val clearCartUseCase: ClearCartUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _effect = Channel<LoginEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _state.update { it.copy(email = event.email) }
                validateForm()
            }

            is LoginEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.password) }
                validateForm()
            }

            LoginEvent.TogglePasswordVisibility -> {
                _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }

            LoginEvent.LoginClicked -> login()
            LoginEvent.ForgotPasswordClicked -> sendEffect(LoginEffect.NavigateToForgotPassword)
            LoginEvent.RegisterClicked -> sendEffect(LoginEffect.NavigateToRegister)
            LoginEvent.GuestLoginClicked -> loginAsGuest()
            is LoginEvent.GoogleLoginTokenReceived -> loginWithGoogle(event.idToken)
            is LoginEvent.GoogleLoginError -> sendEffect(LoginEffect.ShowError(event.message))
        }
    }

    private fun validateForm() {
        val current = _state.value

        val isEmailValid = current.email.isNotBlank() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(current.email).matches()

        val isValid = isEmailValid && current.password.length >= 6

        _state.update { it.copy(isEmailValid = isEmailValid, isLoginEnabled = isValid) }
    }

    private fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            loginUseCase(_state.value.email, _state.value.password)
                .onSuccess { user ->
                    clearCartUseCase()
                    saveAuthenticatedSessionUseCase(user.customerId)

                    linkCartToUserUseCase(_state.value.email)
                        .onFailure { throwable ->
                            Log.e("LoginViewModel", "linkCartToUser failed: ${throwable.message}", throwable)
                        }

                    _state.update { it.copy(isLoading = false) }
                    sendEffect(LoginEffect.NavigateToHome)
                }
                .onFailure { exception ->
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(LoginEffect.ShowError(exception.message ?: "Something went wrong"))
                }
        }
    }

    private fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            loginWithGoogleUseCase(idToken)
                .onSuccess { user ->
                    clearCartUseCase()
                    saveAuthenticatedSessionUseCase(user.customerId)

                    linkCartToUserUseCase(user.email)
                        .onFailure { throwable ->
                            Log.e("LoginViewModel", "linkCartToUser failed: ${throwable.message}", throwable)
                        }

                    _state.update { it.copy(isLoading = false) }
                    sendEffect(LoginEffect.NavigateToHome)
                }
                .onFailure { exception ->
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(LoginEffect.ShowError(exception.message ?: "Google login failed"))
                }
        }
    }

    private fun loginAsGuest() {
        viewModelScope.launch {
            clearCartUseCase()
            saveGuestSessionUseCase()
            refreshCartUseCase()
            sendEffect(LoginEffect.NavigateToHome)
        }
    }

    private fun sendEffect(effect: LoginEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}