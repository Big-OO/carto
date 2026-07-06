package com.shopify.carto.feature.currency.data.remote.model

import com.google.gson.annotations.SerializedName

data class CurrencyResponseDto(
    @SerializedName("base")
    val base: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("time_last_updated")
    val timeLastUpdated: Long,
    @SerializedName("rates")
    val rates: Map<String, Double>
)
