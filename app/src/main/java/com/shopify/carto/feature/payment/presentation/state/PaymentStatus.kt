package com.shopify.carto.feature.payment.presentation.state

sealed interface PaymentStatus {

    data object Idle : PaymentStatus

    data object Processing : PaymentStatus

    data class RequiresPaymobSdk(val clientSecret: String) : PaymentStatus

    data class Success(
        val transactionId: String = "",
        val orderId: String = "",
    ) : PaymentStatus


    data class Failed(val message: String) : PaymentStatus

    data object Cancelled : PaymentStatus
}
