package com.shopify.carto.feature.orderdetails.data.repository

import com.shopify.carto.core.network.config.ShopifyConfig
import com.shopify.carto.feature.orderdetails.data.mapper.toDomain
import com.shopify.carto.feature.orderdetails.data.remote.network.OrderDetailsNetworkDataSource
import com.shopify.carto.feature.orderdetails.domain.model.OrderDetails
import com.shopify.carto.feature.orderdetails.domain.model.OrderDetailsFailure
import com.shopify.carto.feature.orderdetails.domain.model.OrderDetailsFailureType
import com.shopify.carto.feature.orderdetails.domain.model.OrderDetailsResult
import com.shopify.carto.feature.orderdetails.domain.repository.OrderDetailsRepository
import java.io.IOException
import javax.inject.Inject

class OrderDetailsRepositoryImpl @Inject constructor(
    private val networkDataSource: OrderDetailsNetworkDataSource,
    private val config: ShopifyConfig,
) : OrderDetailsRepository {

    override suspend fun getOrderDetails(orderId: String): OrderDetailsResult<OrderDetails> {
        if (!config.isAdminRestValid) {
            return OrderDetailsResult.Failure(
                OrderDetailsFailure(
                    type = OrderDetailsFailureType.ShopifyConfigurationMissing,
                    message = "Shopify Admin GraphQL configuration is missing.",
                )
            )
        }

        return try {
            val response = networkDataSource.getOrderDetails(config.apiVersion, orderId)
            if (!response.isSuccessful) {
                return OrderDetailsResult.Failure(
                    OrderDetailsFailure(
                        type = response.code().toFailureType(),
                        message = "Failed to load order details. code=${response.code()}, body=${response.errorBody()?.string().orEmpty()}",
                    )
                )
            }

            val body = response.body()
            val graphQlError = body?.errors.orEmpty().firstOrNull()?.message
            if (!graphQlError.isNullOrBlank()) {
                return OrderDetailsResult.Failure(
                    OrderDetailsFailure(OrderDetailsFailureType.GraphQl, graphQlError),
                )
            }

            val order = body?.data?.order?.toDomain()
                ?: return OrderDetailsResult.Failure(
                    OrderDetailsFailure(OrderDetailsFailureType.NotFound, "Order details response did not contain an order."),
                )

            OrderDetailsResult.Success(order)
        } catch (exception: IOException) {
            OrderDetailsResult.Failure(
                OrderDetailsFailure(
                    type = OrderDetailsFailureType.Network,
                    message = "Network failure while loading order details: ${exception::class.java.name}. ${exception.message.orEmpty()}",
                )
            )
        } catch (exception: Exception) {
            OrderDetailsResult.Failure(
                OrderDetailsFailure(
                    type = OrderDetailsFailureType.Unknown,
                    message = "Unexpected failure while loading order details: ${exception::class.java.name}. ${exception.message.orEmpty()}",
                )
            )
        }
    }

    override suspend fun cancelOrder(orderId: String): OrderDetailsResult<Unit> {
        if (!config.isAdminRestValid) {
            return OrderDetailsResult.Failure(
                OrderDetailsFailure(
                    type = OrderDetailsFailureType.ShopifyConfigurationMissing,
                    message = "Shopify Admin GraphQL configuration is missing.",
                )
            )
        }

        return try {
            val response = networkDataSource.cancelOrder(config.apiVersion, orderId)
            if (!response.isSuccessful) {
                return OrderDetailsResult.Failure(
                    OrderDetailsFailure(
                        type = response.code().toFailureType(),
                        message = "Failed to cancel order. code=${response.code()}, body=${response.errorBody()?.string().orEmpty()}",
                    )
                )
            }

            val body = response.body()
            val graphQlError = body?.errors.orEmpty().firstOrNull()?.message
            val userError = body?.data?.orderCancel?.orderCancelUserErrors.orEmpty().firstOrNull()?.message
                ?: body?.data?.orderCancel?.userErrors.orEmpty().firstOrNull()?.message

            val message = graphQlError ?: userError
            if (!message.isNullOrBlank()) {
                return OrderDetailsResult.Failure(
                    OrderDetailsFailure(OrderDetailsFailureType.GraphQl, message),
                )
            }

            OrderDetailsResult.Success(Unit)
        } catch (exception: IOException) {
            OrderDetailsResult.Failure(
                OrderDetailsFailure(
                    type = OrderDetailsFailureType.Network,
                    message = "Network failure while cancelling order: ${exception::class.java.name}. ${exception.message.orEmpty()}",
                )
            )
        } catch (exception: Exception) {
            OrderDetailsResult.Failure(
                OrderDetailsFailure(
                    type = OrderDetailsFailureType.Unknown,
                    message = "Unexpected failure while cancelling order: ${exception::class.java.name}. ${exception.message.orEmpty()}",
                )
            )
        }
    }

    private fun Int.toFailureType(): OrderDetailsFailureType {
        return when (this) {
            401, 403 -> OrderDetailsFailureType.Unauthorized
            404 -> OrderDetailsFailureType.NotFound
            in 500..599 -> OrderDetailsFailureType.Server
            else -> OrderDetailsFailureType.Unknown
        }
    }
}
