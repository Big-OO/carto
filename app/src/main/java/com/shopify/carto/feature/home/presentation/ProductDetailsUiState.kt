package com.shopify.carto.feature.home.presentation

import com.shopify.carto.feature.home.domain.model.Product

sealed interface ProductDetailsUiState {
    data object Loading : ProductDetailsUiState
    data class Success(val product: Product) : ProductDetailsUiState
    data class Error(val message: String) : ProductDetailsUiState
}
