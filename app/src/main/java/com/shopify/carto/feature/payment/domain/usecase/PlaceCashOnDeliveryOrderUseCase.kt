package com.shopify.carto.feature.payment.domain.usecase

import com.shopify.carto.feature.payment.domain.model.PaymentError
import com.shopify.carto.feature.payment.domain.model.PaymentRequest
import com.shopify.carto.feature.payment.domain.model.PaymentResult
import com.shopify.carto.feature.payment.domain.repository.PaymentRepository
import javax.inject.Inject

class PlaceCashOnDeliveryOrderUseCase @Inject constructor(
    private val repository: PaymentRepository,
) {
    suspend operator fun invoke(request: PaymentRequest): PaymentResult {
        if (request.amountCents <= 0) {
            return PaymentResult.Failure(
                error = PaymentError.VALIDATION_FAILED,
                message = "Order amount must be greater than zero",
            )
        }

        return repository.placeCashOnDeliveryOrder(request)
    }
}
