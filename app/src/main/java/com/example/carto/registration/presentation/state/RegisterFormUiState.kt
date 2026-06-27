package com.example.carto.registration.presentation.state

import androidx.compose.runtime.Immutable

@Immutable
data class RegisterFormInput(
    val value: String = "",
    val isError: Boolean = false,
    val errorMessage: String = "",
)

@Immutable
data class RegisterFormUiState(
    val fullName: RegisterFormInput = RegisterFormInput(),
    val email: RegisterFormInput = RegisterFormInput(),
    val password: RegisterFormInput = RegisterFormInput(),
    val isLoading: Boolean = false
)