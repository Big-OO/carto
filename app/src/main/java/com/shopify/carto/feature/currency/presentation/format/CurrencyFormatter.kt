package com.shopify.carto.feature.currency.presentation.format

import androidx.compose.runtime.compositionLocalOf
import com.shopify.carto.feature.currency.domain.model.Currency
import com.shopify.carto.feature.currency.domain.model.ExchangeRates

data class CurrencyFormatter(
    val selectedCurrency: Currency = Currency.USD,
    val rates: ExchangeRates? = null
) {
    fun convert(priceUsd: Double): Double {
        if (selectedCurrency == Currency.USD) return priceUsd
        if (rates == null) return priceUsd
        val rate = rates.rates[selectedCurrency] ?: 1.0
        return priceUsd * rate
    }

    fun format(priceUsd: Double): String {
        val converted = convert(priceUsd)
        val rawAmount = "%,.2f".format(java.util.Locale.US, converted)
        return "${selectedCurrency.displayName} $rawAmount"
    }
}

val LocalCurrencyFormatter = compositionLocalOf { CurrencyFormatter() }
