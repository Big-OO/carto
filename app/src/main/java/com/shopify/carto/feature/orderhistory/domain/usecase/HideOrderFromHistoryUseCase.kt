package com.shopify.carto.feature.orderhistory.domain.usecase

import com.shopify.carto.feature.orderhistory.domain.repository.OrderHistoryRepository
import javax.inject.Inject

class HideOrderFromHistoryUseCase @Inject constructor(
    private val repository: OrderHistoryRepository,
) {
    suspend operator fun invoke(orderId: String) = repository.hideOrder(orderId)
}
