package com.shopify.carto.feature.shopping_cart.data.repository

import android.util.Log
import com.shopify.carto.core.common.exception.DataException
import com.shopify.carto.core.session.domain.repository.AppSessionRepository
import com.shopify.carto.feature.shopping_cart.data.datasource.local.CartSessionLocalDataSource
import com.shopify.carto.feature.shopping_cart.data.datasource.remote.CartRemoteDataSource
import com.shopify.carto.feature.shopping_cart.data.datasource.remote.CartSessionRemoteDataSource
import com.shopify.carto.feature.shopping_cart.data.mapper.toDomain
import com.shopify.carto.feature.shopping_cart.domain.model.Cart
import com.shopify.carto.feature.shopping_cart.domain.repository.CartRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "CartRepositoryImpl"

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val remoteDataSource: CartRemoteDataSource,
    private val sessionLocalDataSource: CartSessionLocalDataSource,
    private val sessionRemoteDataSource: CartSessionRemoteDataSource,
    private val appSessionRepository: AppSessionRepository
) : CartRepository {

    private val cartState = MutableStateFlow(Result.success(Cart.empty()))

    override fun observeCart(): Flow<Result<Cart>> = cartState.asStateFlow()

    private suspend fun getCurrentCustomerId(): String? {
        return appSessionRepository.session.first().customerId
    }

    override suspend fun clearCart() {
        cartState.value = Result.success(Cart.empty())
    }

    override suspend fun refreshCart() = withContext(NonCancellable) {
        val customerId = getCurrentCustomerId()
        var cartId = sessionLocalDataSource.getCartId(customerId)

        // Not found on this device: for a logged-in user, check if they have a cart
        // linked from another device.
        if (cartId == null) {
            cartId = sessionRemoteDataSource.getCartId(customerId ?: "").getOrNull()
            cartId?.let { sessionLocalDataSource.saveCartId(it, customerId) }
        }

        if (cartId == null) {
            cartState.value = Result.success(Cart.empty())
            return@withContext
        }

        remoteDataSource.getCart(cartId)
            .onSuccess { cart ->
                if (cart != null) {
                    cartState.value = Result.success(cart.toDomain())
                } else {
                    // Cart no longer exists on Shopify (e.g. expired) — clean up both caches.
                    sessionLocalDataSource.clearCartId(customerId)
                    sessionRemoteDataSource.clearCartId(customerId ?: "")
                    cartState.value = Result.success(Cart.empty())
                }
            }
            .onFailure { throwable ->
                if (throwable is CancellationException || throwable.cause is CancellationException) {
                    return@onFailure
                }
                Log.e(TAG, "refreshCart failed: ${throwable.message}", throwable)
                cartState.value = Result.failure(throwable)
            }
    }

    override suspend fun addLine(merchandiseId: String, quantity: Int): Result<Unit> {
        val cartId = ensureCartId()
            ?: return Result.failure(DataException.Unknown(Exception("Unable to create cart")))

        return remoteDataSource.addLine(cartId, merchandiseId, quantity)
            .onSuccess { refreshCart() }
            .onFailure { throwable -> Log.e(TAG, "addLine failed: ${throwable.message}", throwable) }
    }

    override suspend fun updateLineQuantity(lineId: String, quantity: Int): Result<Unit> {
        val customerId = getCurrentCustomerId()
        val cartId = sessionLocalDataSource.getCartId(customerId)
            ?: return Result.failure(DataException.Unknown(Exception("No active cart")))

        return remoteDataSource.updateLineQuantity(cartId, lineId, quantity)
            .onSuccess { refreshCart() }
            .onFailure { throwable -> Log.e(TAG, "updateLineQuantity failed: ${throwable.message}", throwable) }
    }

    override suspend fun removeLine(lineId: String): Result<Unit> {
        val customerId = getCurrentCustomerId()
        val cartId = sessionLocalDataSource.getCartId(customerId)
            ?: return Result.failure(DataException.Unknown(Exception("No active cart")))

        return remoteDataSource.removeLine(cartId, listOf(lineId))
            .onSuccess { refreshCart() }
            .onFailure { throwable -> Log.e(TAG, "removeLine failed: ${throwable.message}", throwable) }
    }

    override suspend fun linkCartToUser(email: String): Result<Unit> = withContext(NonCancellable) {
        val customerId = getCurrentCustomerId()

        val guestCartId = sessionLocalDataSource.getCartId(null)
        val existingUserCartId = sessionLocalDataSource.getCartId(customerId)
            ?: sessionRemoteDataSource.getCartId(customerId ?: "").getOrNull()

        if (guestCartId == null) {
            if (existingUserCartId != null) {
                sessionLocalDataSource.saveCartId(existingUserCartId, customerId)
                sessionRemoteDataSource.saveCartId(customerId ?: "", existingUserCartId)
            }
            refreshCart()
            return@withContext Result.success(Unit)
        }

        return@withContext remoteDataSource.updateCartBuyerIdentity(guestCartId, email)
            .onSuccess { updatedCartId ->
                sessionLocalDataSource.saveCartId(updatedCartId, customerId)
                sessionRemoteDataSource.saveCartId(customerId ?: "", updatedCartId)
                sessionLocalDataSource.clearCartId(null)
                refreshCart()
            }
            .onFailure { throwable ->
                Log.e(TAG, "linkCartToUser failed, keeping guest cart id intact: ${throwable.message}", throwable)
                val fallbackCartId = existingUserCartId ?: guestCartId
                sessionLocalDataSource.saveCartId(fallbackCartId, customerId)
                sessionRemoteDataSource.saveCartId(customerId ?: "", fallbackCartId)
                refreshCart()
            }
            .map { Unit }
    }

    private suspend fun ensureCartId(): String? {
        val customerId = getCurrentCustomerId()

        sessionLocalDataSource.getCartId(customerId)?.let { return it }

        // Check Firestore before creating a brand-new cart — the user may already
        // have one linked from another device.
        sessionRemoteDataSource.getCartId(customerId ?: "").getOrNull()?.let { remoteCartId ->
            sessionLocalDataSource.saveCartId(remoteCartId, customerId)
            return remoteCartId
        }

        val result = remoteDataSource.createCart()

        result.onFailure { throwable ->
            Log.e(TAG, "Cart creation error: ${throwable.message}", throwable)
        }

        return result
            .onSuccess { cart ->
                sessionLocalDataSource.saveCartId(cart.id, customerId)
                sessionRemoteDataSource.saveCartId(customerId ?: "", cart.id)
            }
            .map { it.id }
            .getOrNull()
    }
}