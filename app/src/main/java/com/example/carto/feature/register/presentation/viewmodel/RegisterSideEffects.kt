package com.example.carto.feature.register.presentation.viewmodel

sealed interface RegisterSideEffects{
    object NavigateToLogin: RegisterSideEffects
}