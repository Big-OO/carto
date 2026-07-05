package com.shopify.carto.feature.shopping_cart.domain.model

data class Cart(
    val id: String,
    val checkoutUrl: String,
    val lines: List<CartLine>,
    val subtotal: Double,
    val total: Double,
    val currency: String,
    val totalQuantity: Int
) {
    val isEmpty: Boolean get() = lines.isEmpty()

    companion object {
        fun empty(currency: String = "EGP"): Cart = Cart(
            id = "",
            checkoutUrl = "",
            lines = emptyList(),
            subtotal = 0.0,
            total = 0.0,
            currency = currency,
            totalQuantity = 0
        )
    }
}