package com.shopify.carto.feature.currency.domain.repository

import com.shopify.carto.feature.currency.domain.model.Currency
import com.shopify.carto.feature.currency.domain.model.ExchangeRates
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    suspend fun refreshRates(): Result<Unit>
    fun observeRates(): Flow<ExchangeRates?>
    fun observeSelectedCurrency(): Flow<Currency>
    suspend fun updateSelectedCurrency(currency: Currency)
    fun convertPrice(priceUsd: Double): Flow<Double>
}
