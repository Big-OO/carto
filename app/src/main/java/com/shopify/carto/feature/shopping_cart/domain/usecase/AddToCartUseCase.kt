package com.shopify.carto.feature.shopping_cart.domain.usecase

import com.shopify.carto.feature.shopping_cart.domain.repository.CartRepository
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(merchandiseId: String, quantity: Int = 1): Result<Unit> {
        return repository.addLine(merchandiseId, quantity)
    }
}