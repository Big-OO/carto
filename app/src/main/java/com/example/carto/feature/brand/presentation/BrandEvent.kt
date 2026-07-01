package com.example.carto.feature.brand.presentation

sealed interface BrandEvent {
    data class LoadBrand(val brandName: String) : BrandEvent
    data class FilterProductType(val productType: String) : BrandEvent
    data object ClickBack : BrandEvent
    data class ClickProduct(val productId: String) : BrandEvent
}
