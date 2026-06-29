package com.example.carto.registration.presentation.utils

import com.example.carto.registration.domain.model.ValidationFormError
import com.example.carto.registration.presentation.uimodels.FieldType


fun ValidationFormError.toUiMessage(fieldType: FieldType): String {
    return when (this) {
        ValidationFormError.Valid -> ""
        ValidationFormError.Empty -> when (fieldType) {
            FieldType.FullName -> "Full name is required."
            FieldType.Email -> "Email is required."
            FieldType.Password -> "Password is required."
        }

        ValidationFormError.Invalid -> when (fieldType) {
            FieldType.FullName -> "Enter your first and last name."
            FieldType.Email -> "Enter a valid email address."
            FieldType.Password -> "Password must be at least 8 characters and include uppercase, lowercase, number, and special character."
        }
    }
}