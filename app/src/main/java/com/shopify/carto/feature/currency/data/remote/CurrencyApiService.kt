package com.shopify.carto.feature.currency.data.remote

import com.shopify.carto.feature.currency.data.remote.model.CurrencyResponseDto
import retrofit2.http.GET

interface CurrencyApiService {
    @GET("latest/USD")
    suspend fun getExchangeRates(): CurrencyResponseDto
}
