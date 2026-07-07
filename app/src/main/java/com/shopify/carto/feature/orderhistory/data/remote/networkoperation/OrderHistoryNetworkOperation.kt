package com.shopify.carto.feature.orderhistory.data.remote.networkoperation

import com.shopify.carto.feature.orderhistory.data.remote.dto.OrderHistoryGraphQlResponseDto

interface OrderHistoryNetworkOperation {
    suspend fun getCustomerOrders(
        customerGid: String,
        first: Int,
    ): OrderHistoryGraphQlResponseDto
}
