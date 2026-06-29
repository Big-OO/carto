package com.example.carto.registration.domain.usecases

import com.example.carto.registration.domain.model.ValidationFormError

class ValidateEmailUseCase {
    operator fun invoke(email: String): ValidationFormError {
        if (email.isEmpty())
            return ValidationFormError.Empty

        if (!Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$\n")
            .matches(email))
            return ValidationFormError.Invalid

        return ValidationFormError.Valid
    }
}