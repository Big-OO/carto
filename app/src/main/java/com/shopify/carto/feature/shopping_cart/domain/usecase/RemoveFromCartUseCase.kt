package com.shopify.carto.feature.shopping_cart.domain.usecase

import com.shopify.carto.feature.shopping_cart.domain.repository.CartRepository
import javax.inject.Inject

class RemoveFromCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(lineId: String): Result<Unit> {
        return repository.removeLine(lineId)
    }
}