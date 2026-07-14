package com.shopify.carto.feature.payment.domain.model

data class OrderItem(
    val name: String,
    val quantity: Int,
    val amountCents: Int,
    val variantId: String = "",
    val imageUrl: String? = null,
    val variantTitle: String = "",
)
