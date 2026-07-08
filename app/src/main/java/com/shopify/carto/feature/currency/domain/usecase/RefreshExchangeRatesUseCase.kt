package com.shopify.carto.feature.currency.domain.usecase

import com.shopify.carto.feature.currency.domain.repository.CurrencyRepository
import javax.inject.Inject

class RefreshExchangeRatesUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.refreshRates()
    }
}
