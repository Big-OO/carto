package com.shopify.carto.feature.payment.domain.usecase

import com.shopify.carto.feature.payment.domain.model.PaymentRequest
import com.shopify.carto.feature.payment.domain.model.PaymentResult
import com.shopify.carto.feature.payment.domain.repository.PaymentRepository
import javax.inject.Inject

class PlaceShopifyOrderUseCase @Inject constructor(
    private val repository: PaymentRepository,
) {
    suspend operator fun invoke(request: PaymentRequest, financialStatus: String): PaymentResult {
        return repository.createShopifyOrder(request, financialStatus)
    }
}
