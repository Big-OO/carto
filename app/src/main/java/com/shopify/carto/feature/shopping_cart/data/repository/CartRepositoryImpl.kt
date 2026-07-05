package com.shopify.carto.feature.shopping_cart.data.repository

import android.util.Log
import com.shopify.carto.core.common.exception.DataException
import com.shopify.carto.feature.shopping_cart.data.datasource.local.CartSessionLocalDataSource
import com.shopify.carto.feature.shopping_cart.data.datasource.remote.CartRemoteDataSource
import com.shopify.carto.feature.shopping_cart.data.mapper.toDomain
import com.shopify.carto.feature.shopping_cart.domain.model.Cart
import com.shopify.carto.feature.shopping_cart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val remoteDataSource: CartRemoteDataSource,
    private val sessionLocalDataSource: CartSessionLocalDataSource
) : CartRepository {

    private val cartState = MutableStateFlow(Result.success(Cart.empty()))

    override fun observeCart(): Flow<Result<Cart>> = cartState.asStateFlow()

    override suspend fun refreshCart() {
        val cartId = sessionLocalDataSource.getCartId() ?: return

        remoteDataSource.getCart(cartId)
            .onSuccess { cart ->
                if (cart != null) {
                    cartState.value = Result.success(cart.toDomain())
                } else {
                    sessionLocalDataSource.clearCartId()
                    cartState.value = Result.success(Cart.empty())
                }
            }
            .onFailure { throwable -> cartState.value = Result.failure(throwable) }
    }

    override suspend fun addLine(merchandiseId: String, quantity: Int): Result<Unit> {
        Log.d("testRepo","$merchandiseId")
        val cartId = ensureCartId()
            ?: return Result.failure(DataException.Unknown(Exception("Unable to create cart")))

        return remoteDataSource.addLine(cartId, merchandiseId, quantity)
            .onSuccess { refreshCart() }
    }

    override suspend fun updateLineQuantity(lineId: String, quantity: Int): Result<Unit> {
        val cartId = sessionLocalDataSource.getCartId()
            ?: return Result.failure(DataException.Unknown(Exception("No active cart")))

        return remoteDataSource.updateLineQuantity(cartId, lineId, quantity)
            .onSuccess { refreshCart() }
    }

    override suspend fun removeLine(lineId: String): Result<Unit> {
        val cartId = sessionLocalDataSource.getCartId()
            ?: return Result.failure(DataException.Unknown(Exception("No active cart")))

        return remoteDataSource.removeLine(cartId, listOf(lineId))
            .onSuccess { refreshCart() }
    }

    private suspend fun ensureCartId(): String? {
        Log.d("id","${sessionLocalDataSource.getCartId()}")
        sessionLocalDataSource.getCartId()?.let { return it }
        val result = remoteDataSource.createCart()

        result.onFailure { throwable ->
            Log.e("CartRepositoryImpl", "Real cart creation error: ${throwable.message}", throwable)
        }

        return result
            .onSuccess { cart -> sessionLocalDataSource.saveCartId(cart.id) }
            .map { it.id }
            .getOrNull()
    }
}