package com.shopify.carto.feature.payment.domain.model


data class PaymentRequest(
    val amountCents: Int,
    val currency: String = "EGP",
    val paymentMethod: PaymentMethod,
    val customerFirstName: String,
    val customerLastName: String,
    val customerEmail: String,
    val customerPhone: String,
    val address: String,
    val city: String,
    val country: String = "EG",
    val items: List<OrderItem> = emptyList(),
    val discountCode: String? = null,
    val discountAmountCents: Int = 0,
)
