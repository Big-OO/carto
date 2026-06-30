package com.example.carto.feature.register.presentation.state

import androidx.compose.runtime.Immutable

@Immutable
data class RegisterFormUiState(
    val fullName: RegisterFormInput = RegisterFormInput(),
    val email: RegisterFormInput = RegisterFormInput(),
    val password: RegisterFormInput = RegisterFormInput(),
    val isLoading: Boolean = false,
    val generalErrorMessage: String = "",
    val isPasswordVisible: Boolean = false,
)
