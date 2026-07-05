package com.shopify.carto.feature.payment.domain.repository

import com.shopify.carto.feature.payment.domain.model.PaymentRequest
import com.shopify.carto.feature.payment.domain.model.PaymentResult

interface PaymentRepository {

    suspend fun createPaymentIntention(request: PaymentRequest): PaymentResult

    suspend fun placeCashOnDeliveryOrder(request: PaymentRequest): PaymentResult

    suspend fun checkPaymentStatus(clientSecret: String): Boolean

    // don't forget to add this functions here Abdelrahman
    // - createShopifyOrder
    // - reduceShopifyInventory

}
