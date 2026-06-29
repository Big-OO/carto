package com.example.carto.registration.domain.usecases

import com.example.carto.registration.domain.model.ValidationFormError

class ValidateFullNameUseCase {
    operator fun invoke(fullName: String): ValidationFormError {
        if (fullName.trim().isEmpty())
            return ValidationFormError.Empty

        if (fullName.trim().split(" ").size < 2)
            return ValidationFormError.Invalid

        return ValidationFormError.Valid
    }
}