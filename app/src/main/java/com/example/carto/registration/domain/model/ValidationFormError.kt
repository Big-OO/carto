package com.example.carto.registration.domain.model

sealed interface ValidationFormError {
    object Empty: ValidationFormError
    object Invalid: ValidationFormError
    object Valid: ValidationFormError
}