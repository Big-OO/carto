package com.shopify.carto.feature.orderhistory.data.remote.network

import com.shopify.carto.feature.orderhistory.data.remote.dto.OrderHistoryGraphQlResponseDto
import retrofit2.Response

interface OrderHistoryNetworkDataSource {
    suspend fun getCustomerOrders(
        version: String,
        customerGid: String,
        first: Int,
    ): Response<OrderHistoryGraphQlResponseDto>
}
