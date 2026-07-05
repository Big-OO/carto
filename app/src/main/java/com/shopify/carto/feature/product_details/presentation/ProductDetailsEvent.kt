package com.shopify.carto.feature.product_details.presentation

sealed interface ProductDetailsEvent {
    data object OnBackClick : ProductDetailsEvent
    data object OnFavoriteClick : ProductDetailsEvent
    data object OnAddToCartClick : ProductDetailsEvent

    data object OnRemoveFromCartConfirm : ProductDetailsEvent
    data object OnRetryClick : ProductDetailsEvent
    data class OnImageSelected(val index: Int) : ProductDetailsEvent
    data class OnSizeSelected(val size: String) : ProductDetailsEvent
    data class OnColorSelected(val color: String) : ProductDetailsEvent
}