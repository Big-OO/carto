package com.shopify.carto.feature.product_details.presentation

sealed interface ProductDetailsEffect {
    data object NavigateBack : ProductDetailsEffect
    data object NavigateToCart : ProductDetailsEffect
    data class ShowError(val messageRes: Int) : ProductDetailsEffect
}