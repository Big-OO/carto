package com.shopify.carto.feature.payment.presentation.state

import com.shopify.carto.feature.payment.domain.model.PaymentMethod


sealed interface CheckoutEvent {
    data class UpdateFirstName(val value: String) : CheckoutEvent
    data class UpdateLastName(val value: String) : CheckoutEvent
    data class UpdateEmail(val value: String) : CheckoutEvent
    data class UpdatePhone(val value: String) : CheckoutEvent
    data class UpdateAddress(val value: String) : CheckoutEvent
    data class UpdateCity(val value: String) : CheckoutEvent

    data class SelectPaymentMethod(val method: PaymentMethod) : CheckoutEvent

    data object PlaceOrder : CheckoutEvent
    data object RetryPayment : CheckoutEvent
}












