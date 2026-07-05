package com.shopify.carto.feature.payment.domain.model

sealed class PaymentResult {

    data class Success(
        val clientSecret: String = "",
        val orderId: String = "",
        val transactionId: String = "",
    ) : PaymentResult()


    data class Failure(
        val error: PaymentError,
        val message: String = error.userMessage,
    ) : PaymentResult()


    data object Cancelled : PaymentResult()
}
