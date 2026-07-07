package com.shopify.carto.feature.orderdetails.data.repository

import com.shopify.carto.core.network.config.ShopifyConfig
import com.shopify.carto.feature.orderdetails.data.mapper.toDomain
import com.shopify.carto.feature.orderdetails.data.remote.datasource.OrderDetailsRemoteDataSource
import com.shopify.carto.feature.orderdetails.domain.model.OrderDetails
import com.shopify.carto.feature.orderdetails.domain.model.OrderDetailsFailure
import com.shopify.carto.feature.orderdetails.domain.model.OrderDetailsFailureType
import com.shopify.carto.feature.orderdetails.domain.model.OrderDetailsResult
import com.shopify.carto.feature.orderdetails.domain.repository.OrderDetailsRepository
import java.io.IOException
import javax.inject.Inject

class OrderDetailsRepositoryImpl @Inject constructor(
    private val remoteDataSource: OrderDetailsRemoteDataSource,
    private val config: ShopifyConfig,
) : OrderDetailsRepository {

    override suspend fun getOrderDetails(orderId: String): OrderDetailsResult<OrderDetails> {
        if (!config.isAdminGraphQlValid) {
            return OrderDetailsResult.Failure(
                OrderDetailsFailure(
                    type = OrderDetailsFailureType.ShopifyConfigurationMissing,
                    message = "Shopify Admin GraphQL configuration is missing.",
                )
            )
        }

        return try {
            val body = remoteDataSource.getOrderDetails(orderId)
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
        if (!config.isAdminGraphQlValid) {
            return OrderDetailsResult.Failure(
                OrderDetailsFailure(
                    type = OrderDetailsFailureType.ShopifyConfigurationMissing,
                    message = "Shopify Admin GraphQL configuration is missing.",
                )
            )
        }

        return try {
            val body = remoteDataSource.cancelOrder(orderId)
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
}