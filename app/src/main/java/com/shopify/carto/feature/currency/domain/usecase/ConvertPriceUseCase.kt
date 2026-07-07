package com.shopify.carto.feature.currency.domain.usecase

import com.shopify.carto.feature.currency.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConvertPriceUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    operator fun invoke(priceUsd: Double): Flow<Double> {
        return repository.convertPrice(priceUsd)
    }
}
