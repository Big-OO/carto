package com.shopify.carto.feature.brand.presentation

sealed interface BrandEffect {
    data object NavigateBack : BrandEffect
    data class NavigateToProductDetail(val productId: Long) : BrandEffect
}
