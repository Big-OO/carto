package com.shopify.carto.feature.orderdetails.data.remote.network

import com.shopify.carto.feature.orderdetails.data.remote.dto.OrderDetailsGraphQlResponseDto
import retrofit2.Response

interface OrderDetailsNetworkDataSource {
    suspend fun getOrderDetails(version: String, orderId: String): Response<OrderDetailsGraphQlResponseDto>
    suspend fun cancelOrder(version: String, orderId: String): Response<OrderDetailsGraphQlResponseDto>
}
