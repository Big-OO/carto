package com.shopify.carto.feature.orderdetails.domain.repository

import com.shopify.carto.feature.orderdetails.domain.model.OrderDetails
import com.shopify.carto.feature.orderdetails.domain.model.OrderDetailsResult

interface OrderDetailsRepository {
    suspend fun getOrderDetails(orderId: String): OrderDetailsResult<OrderDetails>
    suspend fun cancelOrder(orderId: String): OrderDetailsResult<Unit>
}
