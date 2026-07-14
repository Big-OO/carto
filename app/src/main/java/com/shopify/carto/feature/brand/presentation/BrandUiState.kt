package com.shopify.carto.feature.brand.presentation

sealed interface BrandUiState {
    data object Loading : BrandUiState
    
    data class Success(
        val brand: Brand,
        val allProducts: List<Product>,
        val filteredProducts: List<Product>,
        val filterChips: List<String>,
        val selectedChip: String
    ) : BrandUiState

    data class Error(val error: Throwable) : BrandUiState
}
