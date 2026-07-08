package com.shopify.carto.feature.orderdetails.data.remote.datasource

import com.shopify.carto.feature.orderdetails.data.remote.dto.OrderDetailsGraphQlResponseDto

interface OrderDetailsRemoteDataSource {
    suspend fun getOrderDetails(orderId: String): OrderDetailsGraphQlResponseDto
    suspend fun cancelOrder(orderId: String): OrderDetailsGraphQlResponseDto
}
