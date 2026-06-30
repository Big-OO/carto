package com.example.carto.registration.presentation.viewmodel

sealed interface RegisterSideEffects{
    object NavigateToLogin: RegisterSideEffects
}