package com.shopify.carto.feature.currency.domain.usecase

import com.shopify.carto.feature.currency.domain.model.Currency
import com.shopify.carto.feature.currency.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSelectedCurrencyUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    operator fun invoke(): Flow<Currency> {
        return repository.observeSelectedCurrency()
    }
}
