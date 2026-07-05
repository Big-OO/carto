package com.shopify.carto.feature.register.domain.usecases

import com.shopify.carto.feature.register.domain.model.ValidationFormError

class ValidatePasswordUseCase {
    operator fun invoke(password: String): ValidationFormError {
        if (password.isEmpty())
            return ValidationFormError.Empty

        if (!PASSWORD_REGEX.matches(password))
            return ValidationFormError.Invalid

        return ValidationFormError.Valid
    }

    private companion object {
        val PASSWORD_REGEX = Regex("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$")
    }
}