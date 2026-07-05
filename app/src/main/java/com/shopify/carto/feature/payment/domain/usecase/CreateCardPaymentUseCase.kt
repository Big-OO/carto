package com.shopify.carto.feature.payment.domain.usecase

import com.shopify.carto.feature.payment.domain.model.PaymentError
import com.shopify.carto.feature.payment.domain.model.PaymentRequest
import com.shopify.carto.feature.payment.domain.model.PaymentResult
import com.shopify.carto.feature.payment.domain.repository.PaymentRepository
import javax.inject.Inject

class CreateCardPaymentUseCase @Inject constructor(
    private val repository: PaymentRepository,
) {
    suspend operator fun invoke(request: PaymentRequest): PaymentResult {
        if (request.amountCents <= 0) {
            return PaymentResult.Failure(
                error = PaymentError.VALIDATION_FAILED,
                message = "Order amount must be greater than zero",
            )
        }

        if (request.items.isEmpty()) {
            return PaymentResult.Failure(
                error = PaymentError.VALIDATION_FAILED,
                message = "Order must contain at least one item",
            )
        }

        return repository.createPaymentIntention(request)
    }
}
