package com.shopify.carto.feature.shopping_cart.domain.usecase

import com.shopify.carto.feature.shopping_cart.domain.repository.CartRepository
import javax.inject.Inject

class UpdateCartQuantityUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(lineId: String, quantity: Int): Result<Unit> {
        if (quantity <= 0) return repository.removeLine(lineId)
        return repository.updateLineQuantity(lineId, quantity)
    }
}