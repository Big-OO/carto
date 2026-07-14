package com.shopify.carto.feature.payment.domain.usecase

import com.shopify.carto.feature.payment.domain.repository.PaymentRepository
import javax.inject.Inject

class CheckPaymentStatusUseCase @Inject constructor(
    private val repository: PaymentRepository,
) {
    suspend operator fun invoke(clientSecret: String): Boolean {
        if (clientSecret.isBlank()) return false
        return repository.checkPaymentStatus(clientSecret)
    }
}