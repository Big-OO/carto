package com.shopify.carto.feature.payment.presentation.state

import com.shopify.carto.feature.addresses.domain.model.CustomerAddress
import com.shopify.carto.feature.payment.domain.model.OrderItem
import com.shopify.carto.feature.payment.domain.model.PaymentMethod

data class CheckoutUiState(
    val customerFirstName: String = "",
    val customerLastName: String = "",
    val customerEmail: String = "",
    val customerPhone: String = "",
    val address: String = "",
    val city: String = "",
    val selectedAddress: CustomerAddress? = null,
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.CARD,
    val isProcessing: Boolean = false,
    val validationErrors: Map<String, String> = emptyMap(),
    val orderItems: List<OrderItem> = emptyList(),
    val subtotalAmountCents: Int = 0,
    val shippingFeeCents: Int = 8000,
    val discountAmountCents: Int = 0,
    val appliedPromoCode: String? = null,
    val promoCodeInput: String = "",
    val promoCodeError: String? = null,
) {
    val totalAmountCents: Int
        get() = kotlin.math.max(0, subtotalAmountCents - discountAmountCents + shippingFeeCents)

    val isFormValid: Boolean
        get() = customerFirstName.isNotBlank()
            && customerLastName.isNotBlank()
            && customerEmail.isNotBlank()
            && customerPhone.isNotBlank()
            && address.isNotBlank()
            && city.isNotBlank()
}



















