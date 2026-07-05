package com.shopify.carto.feature.register.presentation.viewmodel

sealed interface RegisterSideEffects{
    object NavigateToLogin: RegisterSideEffects
}