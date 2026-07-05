package com.shopify.carto.feature.register.data.shopify.model

data class ShopifyCustomersResponse(
    val customers: List<ShopifyCustomerDto> = emptyList(),
)
