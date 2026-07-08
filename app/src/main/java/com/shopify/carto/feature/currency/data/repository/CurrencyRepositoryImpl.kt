package com.shopify.carto.feature.currency.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shopify.carto.feature.currency.data.local.CurrencyPreferences
import com.shopify.carto.feature.currency.data.remote.CurrencyApiService
import com.shopify.carto.feature.currency.domain.model.Currency
import com.shopify.carto.feature.currency.domain.model.ExchangeRates
import com.shopify.carto.feature.currency.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepositoryImpl @Inject constructor(
    private val apiService: CurrencyApiService,
    private val preferences: CurrencyPreferences,
    private val gson: Gson
) : CurrencyRepository {

    override suspend fun refreshRates(): Result<Unit> {
        return try {
            val response = apiService.getExchangeRates()

            val filteredRates = response.rates.filterKeys { 
                it == "USD" || it == "EGP" || it == "EUR" 
            }
            
            val json = gson.toJson(filteredRates)
            preferences.saveRates(json, response.timeLastUpdated)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeRates(): Flow<ExchangeRates?> {
        return preferences.ratesJsonFlow.combine(preferences.lastUpdatedFlow) { json, lastUpdated ->
            if (json == null) return@combine null
            
            val type = object : TypeToken<Map<String, Double>>() {}.type
            val rawMap: Map<String, Double> = gson.fromJson(json, type)
            
            val mappedRates = rawMap.mapNotNull { (key, value) ->
                val currency = try {
                    Currency.valueOf(key)
                } catch (e: Exception) {
                    null
                }
                if (currency != null) currency to value else null
            }.toMap()
            
            ExchangeRates(
                base = Currency.USD,
                rates = mappedRates,
                lastUpdated = lastUpdated
            )
        }
    }

    override fun observeSelectedCurrency(): Flow<Currency> {
        return preferences.selectedCurrencyFlow.map { 
            try {
                Currency.valueOf(it)
            } catch (e: Exception) {
                Currency.USD
            }
        }
    }

    override suspend fun updateSelectedCurrency(currency: Currency) {
        preferences.saveSelectedCurrency(currency.name)
    }

    override fun convertPrice(priceUsd: Double): Flow<Double> {
        return observeSelectedCurrency().combine(observeRates()) { selectedCurrency, rates ->
            if (selectedCurrency == Currency.USD) return@combine priceUsd
            if (rates == null) return@combine priceUsd
            
            val rate = rates.rates[selectedCurrency] ?: 1.0
            priceUsd * rate
        }
    }
}
