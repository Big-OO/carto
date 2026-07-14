package com.shopify.carto.feature.orderdetails.domain.usecase

import com.shopify.carto.feature.orderdetails.domain.repository.OrderDetailsRepository
import javax.inject.Inject

class CancelOrderUseCase @Inject constructor(
    private val repository: OrderDetailsRepository,
) {
    suspend operator fun invoke(orderId: String) = repository.cancelOrder(orderId)
}
