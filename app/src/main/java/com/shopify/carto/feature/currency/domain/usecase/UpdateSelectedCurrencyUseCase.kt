package com.shopify.carto.feature.currency.domain.usecase

import com.shopify.carto.feature.currency.domain.model.Currency
import com.shopify.carto.feature.currency.domain.repository.CurrencyRepository
import javax.inject.Inject

class UpdateSelectedCurrencyUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(currency: Currency) {
        repository.updateSelectedCurrency(currency)
    }
}
