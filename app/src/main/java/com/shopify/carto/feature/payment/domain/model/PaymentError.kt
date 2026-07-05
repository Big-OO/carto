package com.shopify.carto.feature.payment.domain.model


enum class PaymentError(val userMessage: String) {
    NETWORK("No internet connection. Please check your network and try again."),
    TIMEOUT("The request timed out. Please try again."),
    PAYMENT_DECLINED("Payment was declined. Please try a different card."),
    INVALID_RESPONSE("Something went wrong. Please try again."),
    CANCELLED("Payment was cancelled."),
    SERVER_ERROR("Payment service is temporarily unavailable. Please try later."),
    VALIDATION_FAILED("Please fill in all required fields correctly."),
    UNKNOWN("An unexpected error occurred. Please try again."),
}
