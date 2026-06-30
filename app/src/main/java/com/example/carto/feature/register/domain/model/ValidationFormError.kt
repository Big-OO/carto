package com.example.carto.feature.register.domain.model

sealed interface ValidationFormError {
    object Empty: ValidationFormError
    object Invalid: ValidationFormError
    object Valid: ValidationFormError
}