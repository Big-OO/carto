package com.shopify.carto.feature.currency.domain.usecase

import com.shopify.carto.feature.currency.domain.model.ExchangeRates
import com.shopify.carto.feature.currency.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExchangeRatesUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    operator fun invoke(): Flow<ExchangeRates?> {
        return repository.observeRates()
    }
}
