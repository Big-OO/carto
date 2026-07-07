package com.shopify.carto.feature.currency.domain.model

data class ExchangeRates(
    val base: Currency = Currency.USD,
    val rates: Map<Currency, Double>,
    val lastUpdated: Long
)
