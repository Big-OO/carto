package com.shopify.carto.feature.payment.domain.repository

import com.shopify.carto.feature.payment.domain.model.PromoCodeResult
import com.shopify.carto.feature.payment.domain.model.PaymentRequest
import com.shopify.carto.feature.payment.domain.model.PaymentResult

interface PaymentRepository {

    suspend fun createPaymentIntention(request: PaymentRequest): PaymentResult

    suspend fun placeCashOnDeliveryOrder(request: PaymentRequest): PaymentResult

    suspend fun checkPaymentStatus(clientSecret: String): Boolean

    suspend fun createShopifyOrder(request: PaymentRequest, financialStatus: String): PaymentResult

    suspend fun applyPromoCode(code: String, subtotalCents: Int): PromoCodeResult
}
