package com.shopify.carto.feature.orderdetails.data.remote.datasource

import com.shopify.carto.feature.orderdetails.data.remote.dto.OrderDetailsGraphQlResponseDto
import com.shopify.carto.feature.orderdetails.data.remote.networkoperation.OrderDetailsNetworkOperation
import javax.inject.Inject

class OrderDetailsRemoteDataSourceImpl @Inject constructor(
    private val networkOperation: OrderDetailsNetworkOperation,
) : OrderDetailsRemoteDataSource {

    override suspend fun getOrderDetails(orderId: String): OrderDetailsGraphQlResponseDto {
        return networkOperation.getOrderDetails(orderId)
    }

    override suspend fun cancelOrder(orderId: String): OrderDetailsGraphQlResponseDto {
        return networkOperation.cancelOrder(orderId)
    }
}
