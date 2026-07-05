package com.shopify.carto.feature.payment.presentation.state

sealed interface CheckoutUiEvent {
    data class LaunchPaymob(val clientSecret: String) : CheckoutUiEvent
    data class PaymentSuccess(val transactionId: String, val orderId: String) : CheckoutUiEvent
    data class PaymentFailed(val message: String) : CheckoutUiEvent
}
