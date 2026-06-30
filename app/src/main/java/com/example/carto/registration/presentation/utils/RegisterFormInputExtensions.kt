package com.example.carto.registration.presentation.utils

import com.example.carto.registration.domain.model.ValidationFormError
import com.example.carto.registration.presentation.state.RegisterFormInput
import com.example.carto.registration.presentation.uimodels.FieldType

fun RegisterFormInput.toValidatedInput(
    validationError: ValidationFormError,
    fieldType: FieldType,
): RegisterFormInput {
    return copy(
        isError = validationError != ValidationFormError.Valid,
        errorMessage = validationError.toUiMessage(fieldType),
    )
}