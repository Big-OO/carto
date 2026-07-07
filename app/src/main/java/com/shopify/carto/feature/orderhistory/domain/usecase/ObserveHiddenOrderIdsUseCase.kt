package com.shopify.carto.feature.orderhistory.domain.usecase

import com.shopify.carto.feature.orderhistory.domain.repository.OrderHistoryRepository
import javax.inject.Inject

class ObserveHiddenOrderIdsUseCase @Inject constructor(
    private val repository: OrderHistoryRepository,
) {
    operator fun invoke() = repository.observeHiddenOrderIds()
}
