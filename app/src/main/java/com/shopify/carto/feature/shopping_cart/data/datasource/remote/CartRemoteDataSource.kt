package com.shopify.carto.feature.shopping_cart.data.datasource.remote

import com.shopify.carto.core.graphql.shopify.CartCreateMutation
import com.shopify.carto.core.graphql.shopify.GetCartQuery


interface CartRemoteDataSource {
    suspend fun createCart(): Result<CartCreateMutation.Cart>
    suspend fun getCart(cartId: String): Result<GetCartQuery.Cart?>
    suspend fun addLine(cartId: String, merchandiseId: String, quantity: Int): Result<Unit>
    suspend fun updateLineQuantity(cartId: String, lineId: String, quantity: Int): Result<Unit>
    suspend fun removeLine(cartId: String, lineIds: List<String>): Result<Unit>
}