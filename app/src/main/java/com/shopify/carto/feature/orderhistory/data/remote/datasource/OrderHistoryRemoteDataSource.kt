package com.shopify.carto.feature.orderhistory.data.remote.datasource

import com.shopify.carto.feature.orderhistory.data.remote.dto.OrderHistoryGraphQlResponseDto

interface OrderHistoryRemoteDataSource {
    suspend fun getCustomerOrders(
        customerGid: String,
        first: Int,
    ): OrderHistoryGraphQlResponseDto
}
