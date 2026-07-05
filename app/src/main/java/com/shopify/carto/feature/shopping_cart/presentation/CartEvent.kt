package com.shopify.carto.feature.shopping_cart.presentation

sealed interface CartEvent {

    data object OnCheckoutClick : CartEvent
    data class OnIncreaseQuantity(val lineId: String, val currentQuantity: Int) : CartEvent
    data class OnDecreaseQuantity(val lineId: String, val currentQuantity: Int) : CartEvent
    data class OnRemoveLine(val lineId: String) : CartEvent
}