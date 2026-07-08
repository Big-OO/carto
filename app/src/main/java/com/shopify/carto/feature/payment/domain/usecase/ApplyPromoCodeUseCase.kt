package com.shopify.carto.feature.payment.domain.usecase

import com.shopify.carto.feature.payment.domain.model.PromoCodeResult
import com.shopify.carto.feature.payment.domain.repository.PaymentRepository
import javax.inject.Inject

class ApplyPromoCodeUseCase @Inject constructor(
    private val repository: PaymentRepository,
) {
    suspend operator fun invoke(code: String, subtotalCents: Int): PromoCodeResult {
        if (code.isBlank()) {
            return PromoCodeResult(isValid = false, errorMessage = "Please enter a promo code")
        }
        return repository.applyPromoCode(code.trim(), subtotalCents)
    }
}
