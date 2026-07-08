package com.shopify.carto.feature.shopping_cart.data.datasource.remote

interface CartSessionRemoteDataSource {
    suspend fun getCartId(customerId: String): Result<String?>
    suspend fun saveCartId(customerId: String, cartId: String): Result<Unit>
    suspend fun clearCartId(customerId: String): Result<Unit>
}