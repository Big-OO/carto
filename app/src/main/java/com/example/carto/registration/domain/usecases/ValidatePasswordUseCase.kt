package com.example.carto.registration.domain.usecases

import com.example.carto.registration.domain.model.ValidationFormError

class ValidatePasswordUseCase {
    operator fun invoke(password: String): ValidationFormError {
        if (password.isEmpty())
            return ValidationFormError.Empty

        if (!Regex("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$")
                .matches(password))
            return ValidationFormError.Invalid

        return ValidationFormError.Valid
    }
}