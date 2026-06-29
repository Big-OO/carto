package com.example.carto.registration.domain.usecases

import com.example.carto.registration.domain.model.ValidationFormError

class ValidateFullNameUseCase {
    operator fun invoke(fullName: String): ValidationFormError {
        val trimmedFullName = fullName.trim()

        if (trimmedFullName.isEmpty()) {
            return ValidationFormError.Empty
        }

        if (trimmedFullName.split(Regex("\\s+")).size < 2) {
            return ValidationFormError.Invalid
        }

        return ValidationFormError.Valid
    }
}
