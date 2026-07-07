package com.shopify.carto.feature.orderhistory.domain.repository

import com.shopify.carto.feature.orderhistory.domain.model.OrderHistoryItem
import com.shopify.carto.feature.orderhistory.domain.model.OrderHistoryResult
import kotlinx.coroutines.flow.Flow

interface OrderHistoryRepository {
    suspend fun getCustomerOrders(customerId: Long): OrderHistoryResult<List<OrderHistoryItem>>
    fun observeHiddenOrderIds(): Flow<Set<String>>
    suspend fun hideOrder(orderId: String): OrderHistoryResult<Unit>
}
