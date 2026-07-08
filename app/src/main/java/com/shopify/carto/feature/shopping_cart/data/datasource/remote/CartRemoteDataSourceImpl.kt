package com.shopify.carto.feature.shopping_cart.data.datasource.remote

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.shopify.carto.core.common.exception.DataException
import com.shopify.carto.core.graphql.shopify.CartCreateMutation
import com.shopify.carto.core.graphql.shopify.CartLinesAddMutation
import com.shopify.carto.core.graphql.shopify.CartLinesRemoveMutation
import com.shopify.carto.core.graphql.shopify.CartLinesUpdateMutation
import com.shopify.carto.core.graphql.shopify.GetCartQuery
import com.shopify.carto.core.graphql.shopify.UpdateCartBuyerIdentityMutation
import com.shopify.carto.core.graphql.shopify.type.CartBuyerIdentityInput
import com.shopify.carto.core.graphql.shopify.type.CartInput
import com.shopify.carto.core.graphql.shopify.type.CartLineInput
import com.shopify.carto.core.graphql.shopify.type.CartLineUpdateInput
import com.shopify.carto.core.graphql.shopify.type.CountryCode

import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

class CartRemoteDataSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient
) : CartRemoteDataSource {

    override suspend fun createCart(): Result<CartCreateMutation.Cart> {
        return try {
            val response = apolloClient.mutation(
                CartCreateMutation(CartInput(lines = Optional.absent()))
            ).execute()
            val cart = response.data?.cartCreate?.cart
            Log.d("id","${cart?.id}")
            val userErrors = response.data?.cartCreate?.userErrors.orEmpty()

            when {
                response.hasErrors() -> Result.failure(gqlError(response.errors?.firstOrNull()?.message))
                userErrors.isNotEmpty() -> Result.failure(gqlError(userErrors.first().message))
                cart != null -> Result.success(cart)
                else -> Result.failure(gqlError("Cart creation failed"))
            }
        } catch (exception: IOException) {
            Result.failure(DataException.Network(exception))
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure(DataException.Unknown(exception))
        }
    }

    override suspend fun getCart(cartId: String): Result<GetCartQuery.Cart?> {
        return try {
            val response = apolloClient.query(GetCartQuery(cartId)).execute()
            if (response.hasErrors()) {
                Result.failure(gqlError(response.errors?.firstOrNull()?.message))
            } else {
                Result.success(response.data?.cart)
            }
        } catch (exception: IOException) {
            Result.failure(DataException.Network(exception))
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure(DataException.Unknown(exception))
        }
    }

    override suspend fun addLine(cartId: String, merchandiseId: String, quantity: Int): Result<Unit> {
        return try {
            val response = apolloClient.mutation(
                CartLinesAddMutation(
                    cartId = cartId,
                    lines = listOf(
                        CartLineInput(
                            merchandiseId = merchandiseId,
                            quantity = Optional.present(quantity)
                        )
                    )
                )
            ).execute()

            val userErrors = response.data?.cartLinesAdd?.userErrors.orEmpty()
            if (response.hasErrors() || userErrors.isNotEmpty()) {
                Result.failure(gqlError(userErrors.firstOrNull()?.message ?: response.errors?.firstOrNull()?.message))
            } else {
                Result.success(Unit)
            }
        } catch (exception: IOException) {
            Result.failure(DataException.Network(exception))
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure(DataException.Unknown(exception))
        }
    }

    override suspend fun updateLineQuantity(cartId: String, lineId: String, quantity: Int): Result<Unit> {
        return try {
            val response = apolloClient.mutation(
                CartLinesUpdateMutation(
                    cartId = cartId,
                    lines = listOf(
                        CartLineUpdateInput(id = lineId, quantity = Optional.present(quantity))
                    )
                )
            ).execute()

            val userErrors = response.data?.cartLinesUpdate?.userErrors.orEmpty()
            if (response.hasErrors() || userErrors.isNotEmpty()) {
                Result.failure(gqlError(userErrors.firstOrNull()?.message ?: response.errors?.firstOrNull()?.message))
            } else {
                Result.success(Unit)
            }
        } catch (exception: IOException) {
            Result.failure(DataException.Network(exception))
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure(DataException.Unknown(exception))
        }
    }

    override suspend fun removeLine(cartId: String, lineIds: List<String>): Result<Unit> {
        return try {
            val response = apolloClient.mutation(
                CartLinesRemoveMutation(cartId = cartId, lineIds = lineIds)
            ).execute()

            val userErrors = response.data?.cartLinesRemove?.userErrors.orEmpty()
            if (response.hasErrors() || userErrors.isNotEmpty()) {
                Result.failure(gqlError(userErrors.firstOrNull()?.message ?: response.errors?.firstOrNull()?.message))
            } else {
                Result.success(Unit)
            }
        } catch (exception: IOException) {
            Result.failure(DataException.Network(exception))
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure(DataException.Unknown(exception))
        }
    }

    private fun gqlError(message: String?): DataException {
        return DataException.Unknown(Exception(message ?: "Unknown GraphQL error"))
    }

    override suspend fun updateCartBuyerIdentity(cartId: String, email: String, countryCode: String): Result<String> {
        return try {
            val response = apolloClient.mutation(
                UpdateCartBuyerIdentityMutation(
                    cartId = cartId,
                    buyerIdentity = CartBuyerIdentityInput(
                        email = Optional.present(email),
                        countryCode = Optional.present(CountryCode.valueOf(countryCode))
                    )
                )
            ).execute()

            val userErrors = response.data?.cartBuyerIdentityUpdate?.userErrors.orEmpty()
            val updatedCartId = response.data?.cartBuyerIdentityUpdate?.cart?.id

            when {
                response.hasErrors() -> Result.failure(gqlError(response.errors?.firstOrNull()?.message))
                userErrors.isNotEmpty() -> Result.failure(gqlError(userErrors.first().message))
                updatedCartId != null -> Result.success(updatedCartId)
                else -> Result.failure(gqlError("Failed to update buyer identity"))
            }
        } catch (exception: IOException) {
            Result.failure(DataException.Network(exception))
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure(DataException.Unknown(exception))
        }
    }
}