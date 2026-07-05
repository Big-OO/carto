package com.shopify.carto.feature.payment.presentation.state

import com.shopify.carto.feature.payment.domain.model.OrderItem
import com.shopify.carto.feature.payment.domain.model.PaymentMethod

data class CheckoutUiState(
    val customerFirstName: String = "e",
    val customerLastName: String = "e",
    val customerEmail: String = "abdallahelsobky02@gmail.com",
    val customerPhone: String = "01226022955",
    val address: String = "fd",
    val city: String = "sd",
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.CARD,
    val isProcessing: Boolean = false,
    val validationErrors: Map<String, String> = emptyMap(),
    val orderItems: List<OrderItem> = sampleOrderItems(),
    val totalAmountCents: Int = sampleOrderItems().sumOf { it.amountCents * it.quantity },
) {
    val isFormValid: Boolean
        get() = customerFirstName.isNotBlank()
            && customerLastName.isNotBlank()
            && customerEmail.isNotBlank()
            && customerPhone.isNotBlank()
            && address.isNotBlank()
            && city.isNotBlank()
}

// remove this for test Abdelrahman
private fun sampleOrderItems(): List<OrderItem> = listOf(
    OrderItem(name = "Sample Product", quantity = 1, amountCents = 10000),
)



















