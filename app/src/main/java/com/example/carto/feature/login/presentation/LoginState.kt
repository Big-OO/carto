package com.example.carto.feature.login.presentation

import androidx.compose.runtime.Immutable

@Immutable
data class LoginState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isEmailValid: Boolean = false,
    val isLoginEnabled: Boolean = false
)