package com.shopify.carto.feature.shopping_cart.domain.model

data class CartLine(
    val id: String,
    val merchandiseId: String,
    val productTitle: String,
    val variantTitle: String,
    val imageUrl: String?,
    val price: Double,
    val quantity: Int
) {
    val lineTotal: Double get() = price * quantity
}