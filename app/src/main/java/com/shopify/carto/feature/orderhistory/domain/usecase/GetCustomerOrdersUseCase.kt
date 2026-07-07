package com.shopify.carto.feature.orderhistory.domain.usecase

import com.shopify.carto.feature.orderhistory.domain.model.OrderHistoryItem
import com.shopify.carto.feature.orderhistory.domain.model.OrderHistoryResult
import com.shopify.carto.feature.orderhistory.domain.repository.OrderHistoryRepository
import javax.inject.Inject

class GetCustomerOrdersUseCase @Inject constructor(
    private val repository: OrderHistoryRepository,
) {
    suspend operator fun invoke(customerId: Long): OrderHistoryResult<List<OrderHistoryItem>> {
        return repository.getCustomerOrders(customerId)
    }
}
