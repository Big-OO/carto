package com.shopify.carto.feature.payment.domain.usecase

sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val errors: Map<String, String>) : ValidationResult()
}

// handle the validation to math your checkout fields Abdelrahman
class ValidateCheckoutUseCase {

    operator fun invoke(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        address: String,
        city: String,
    ): ValidationResult {
        val errors = mutableMapOf<String, String>()

        if (firstName.isBlank()) {
            errors["firstName"] = "First name is required"
        }
        if (lastName.isBlank()) {
            errors["lastName"] = "Last name is required"
        }
        if (email.isBlank()) {
            errors["email"] = "Email is required"
        } else if (!email.matches(EMAIL_REGEX)) {
            errors["email"] = "Please enter a valid email address"
        }
        if (phone.isBlank()) {
            errors["phone"] = "Phone number is required"
        } else if (phone.length < 10) {
            errors["phone"] = "Please enter a valid phone number"
        }
        if (address.isBlank()) {
            errors["address"] = "Address is required"
        }
        if (city.isBlank()) {
            errors["city"] = "City is required"
        }

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
