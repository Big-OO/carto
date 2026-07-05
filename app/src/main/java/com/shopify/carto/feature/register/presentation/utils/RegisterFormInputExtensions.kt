package com.shopify.carto.feature.register.presentation.utils

import com.shopify.carto.feature.register.domain.model.ValidationFormError
import com.shopify.carto.feature.register.presentation.state.RegisterFormInput
import com.shopify.carto.feature.register.presentation.uimodels.FieldType

fun RegisterFormInput.toValidatedInput(
    validationError: ValidationFormError,
    fieldType: FieldType,
): RegisterFormInput {
    return copy(
        isError = validationError != ValidationFormError.Valid,
        errorMessage = validationError.toUiMessage(fieldType),
    )
}