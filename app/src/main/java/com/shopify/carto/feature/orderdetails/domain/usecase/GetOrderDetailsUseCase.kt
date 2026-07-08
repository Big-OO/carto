package com.shopify.carto.feature.orderdetails.domain.usecase

import com.shopify.carto.feature.orderdetails.domain.repository.OrderDetailsRepository
import javax.inject.Inject

class GetOrderDetailsUseCase @Inject constructor(
    private val repository: OrderDetailsRepository,
) {
    suspend operator fun invoke(orderId: String) = repository.getOrderDetails(orderId)
}
