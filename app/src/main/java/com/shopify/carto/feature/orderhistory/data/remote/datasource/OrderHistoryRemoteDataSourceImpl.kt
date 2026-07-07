package com.shopify.carto.feature.orderhistory.data.remote.datasource

import com.shopify.carto.feature.orderhistory.data.remote.dto.OrderHistoryGraphQlResponseDto
import com.shopify.carto.feature.orderhistory.data.remote.networkoperation.OrderHistoryNetworkOperation
import javax.inject.Inject

class OrderHistoryRemoteDataSourceImpl @Inject constructor(
    private val networkOperation: OrderHistoryNetworkOperation,
) : OrderHistoryRemoteDataSource {

    override suspend fun getCustomerOrders(
        customerGid: String,
        first: Int,
    ): OrderHistoryGraphQlResponseDto {
        return networkOperation.getCustomerOrders(
            customerGid = customerGid,
            first = first,
        )
    }
}
