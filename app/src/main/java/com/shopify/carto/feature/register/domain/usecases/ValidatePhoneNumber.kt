package com.shopify.carto.feature.register.domain.usecases

import com.shopify.carto.feature.register.domain.model.ValidationFormError

class ValidatePhoneNumber {
    operator fun invoke(phoneNumber: String): ValidationFormError {
        if (phoneNumber.isEmpty()) return ValidationFormError.Empty

        if (!PHONE_NUMBER_REGEX.matches(phoneNumber))
            return ValidationFormError.Invalid

        return ValidationFormError.Valid
    }

    private companion object {
        val PHONE_NUMBER_REGEX = Regex("^1[0125][0-9]{8}$")
    }
}