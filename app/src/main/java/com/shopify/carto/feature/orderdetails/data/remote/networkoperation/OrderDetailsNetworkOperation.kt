package com.shopify.carto.feature.orderdetails.data.remote.networkoperation

import com.shopify.carto.feature.orderdetails.data.remote.dto.OrderDetailsGraphQlResponseDto

interface OrderDetailsNetworkOperation {
    suspend fun getOrderDetails(orderId: String): OrderDetailsGraphQlResponseDto
    suspend fun cancelOrder(orderId: String): OrderDetailsGraphQlResponseDto
}
