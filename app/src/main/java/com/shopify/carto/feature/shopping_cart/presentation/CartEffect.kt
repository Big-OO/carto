package com.shopify.carto.feature.shopping_cart.presentation

sealed interface CartEffect {
    data class NavigateToCheckout(val checkoutUrl: String) : CartEffect
    data class ShowError(val messageRes: Int) : CartEffect
}