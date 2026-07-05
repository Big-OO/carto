package com.shopify.carto.feature.register.presentation.state

import androidx.compose.runtime.Immutable

@Immutable
data class RegisterFormInput(
    val value: String = "",
    val isError: Boolean = false,
    val errorMessage: String = "",
)
