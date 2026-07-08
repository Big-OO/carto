package com.shopify.carto.feature.shopping_cart.domain.repository

import com.shopify.carto.feature.shopping_cart.domain.model.Cart
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun observeCart(): Flow<Result<Cart>>
    suspend fun refreshCart()
    suspend fun clearCart()
    suspend fun addLine(merchandiseId: String, quantity: Int): Result<Unit>
    suspend fun updateLineQuantity(lineId: String, quantity: Int): Result<Unit>
    suspend fun removeLine(lineId: String): Result<Unit>
    suspend fun linkCartToUser(email: String): Result<Unit>

}