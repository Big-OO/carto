package com.shopify.carto.feature.orderhistory.data.repository

import com.shopify.carto.core.network.config.ShopifyConfig
import com.shopify.carto.feature.orderhistory.data.local.HiddenOrdersLocalDataSource
import com.shopify.carto.feature.orderhistory.data.mapper.toDomain
import com.shopify.carto.feature.orderhistory.data.remote.network.OrderHistoryNetworkDataSource
import com.shopify.carto.feature.orderhistory.domain.model.OrderHistoryFailure
import com.shopify.carto.feature.orderhistory.domain.model.OrderHistoryFailureType
import com.shopify.carto.feature.orderhistory.domain.model.OrderHistoryItem
import com.shopify.carto.feature.orderhistory.domain.model.OrderHistoryResult
import com.shopify.carto.feature.orderhistory.domain.repository.OrderHistoryRepository
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import javax.inject.Inject

class OrderHistoryRepositoryImpl @Inject constructor(
    private val networkDataSource: OrderHistoryNetworkDataSource,
    private val hiddenOrdersLocalDataSource: HiddenOrdersLocalDataSource,
    private val config: ShopifyConfig,
) : OrderHistoryRepository {

    override suspend fun getCustomerOrders(customerId: Long): OrderHistoryResult<List<OrderHistoryItem>> {
        if (!config.isAdminRestValid) {
            return OrderHistoryResult.Failure(
                OrderHistoryFailure(
                    type = OrderHistoryFailureType.ShopifyConfigurationMissing,
                    message = "Shopify Admin GraphQL configuration is missing.",
                )
            )
        }

        return try {
            val response = networkDataSource.getCustomerOrders(
                version = config.apiVersion,
                customerGid = "gid://shopify/Customer/$customerId",
                first = ORDER_LIMIT,
            )

            if (!response.isSuccessful) {
                return OrderHistoryResult.Failure(
                    OrderHistoryFailure(
                        type = response.code().toFailureType(),
                        message = "Failed to load customer orders. code=${response.code()}, body=${response.errorBody()?.string().orEmpty()}",
                    )
                )
            }

            val body = response.body()
            val graphQlError = body?.errors.orEmpty().firstOrNull()?.message
            if (!graphQlError.isNullOrBlank()) {
                return OrderHistoryResult.Failure(
                    OrderHistoryFailure(
                        type = OrderHistoryFailureType.GraphQl,
                        message = graphQlError,
                    )
                )
            }

            val orders = body
                ?.data
                ?.customer
                ?.orders
                ?.nodes
                .orEmpty()
                .mapNotNull { it.toDomain() }

            OrderHistoryResult.Success(orders)
        } catch (exception: IOException) {
            OrderHistoryResult.Failure(
                OrderHistoryFailure(
                    type = OrderHistoryFailureType.Network,
                    message = "Network failure while loading orders: ${exception::class.java.name}. ${exception.message.orEmpty()}",
                )
            )
        } catch (exception: Exception) {
            OrderHistoryResult.Failure(
                OrderHistoryFailure(
                    type = OrderHistoryFailureType.Unknown,
                    message = "Unexpected failure while loading orders: ${exception::class.java.name}. ${exception.message.orEmpty()}",
                )
            )
        }
    }

    override fun observeHiddenOrderIds(): Flow<Set<String>> {
        return hiddenOrdersLocalDataSource.observeHiddenOrderIds()
    }

    override suspend fun hideOrder(orderId: String): OrderHistoryResult<Unit> {
        return try {
            hiddenOrdersLocalDataSource.hideOrder(orderId)
            OrderHistoryResult.Success(Unit)
        } catch (exception: Exception) {
            OrderHistoryResult.Failure(
                OrderHistoryFailure(
                    type = OrderHistoryFailureType.Unknown,
                    message = "Failed to hide order locally: ${exception::class.java.name}. ${exception.message.orEmpty()}",
                )
            )
        }
    }

    private fun Int.toFailureType(): OrderHistoryFailureType {
        return when (this) {
            401, 403 -> OrderHistoryFailureType.Unauthorized
            404 -> OrderHistoryFailureType.NotFound
            in 500..599 -> OrderHistoryFailureType.Server
            else -> OrderHistoryFailureType.Unknown
        }
    }

    private companion object {
        const val ORDER_LIMIT = 50
    }
}
