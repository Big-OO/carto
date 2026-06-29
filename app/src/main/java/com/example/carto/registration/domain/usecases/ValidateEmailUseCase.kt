package com.example.carto.registration.domain.usecases

import com.example.carto.registration.domain.model.ValidationFormError

class ValidateEmailUseCase {
    operator fun invoke(email: String): ValidationFormError {
        val trimmedEmail = email.trim()

        if (trimmedEmail.isEmpty()) {
            return ValidationFormError.Empty
        }

        if (!EMAIL_REGEX.matches(trimmedEmail)) {
            return ValidationFormError.Invalid
        }

        return ValidationFormError.Valid
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
