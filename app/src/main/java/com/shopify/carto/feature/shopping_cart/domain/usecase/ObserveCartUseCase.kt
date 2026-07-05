package com.shopify.carto.feature.shopping_cart.domain.usecase

import com.shopify.carto.feature.shopping_cart.domain.model.Cart
import com.shopify.carto.feature.shopping_cart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    operator fun invoke(): Flow<Result<Cart>> = repository.observeCart()
}