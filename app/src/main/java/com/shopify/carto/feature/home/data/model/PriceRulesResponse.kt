package com.shopify.carto.feature.home.data.model

import com.google.gson.annotations.SerializedName

data class PriceRulesResponse(
    @SerializedName("price_rules") val priceRules: List<PriceRuleDto> = emptyList(),
)

data class PriceRuleDto(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String?,
    @SerializedName("value_type") val valueType: String?,
    @SerializedName("value") val value: String?,
    @SerializedName("usage_limit") val usageLimit: Int?,
    @SerializedName("once_per_customer") val oncePerCustomer: Boolean?,
    @SerializedName("starts_at") val startsAt: String?,
    @SerializedName("ends_at") val endsAt: String?,
)
