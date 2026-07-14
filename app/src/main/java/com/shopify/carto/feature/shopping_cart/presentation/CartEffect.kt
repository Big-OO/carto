package com.shopify.carto.feature.shopping_cart.presentation

sealed interface CartEffect {
    data object NavigateToCheckout : CartEffect
    data class ShowError(val messageRes: Int) : CartEffect
}
