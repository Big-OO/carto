package com.shopify.carto.feature.home.presentation

import com.shopify.carto.feature.home.domain.model.Product

sealed interface CategoryProductsUiState {

    data object Loading : CategoryProductsUiState

    data class Success(

        val products: List<Product>,

        val chips: List<String>,

        val selectedChip: String

    ) : CategoryProductsUiState

    data class Error(
        val message: String
    ) : CategoryProductsUiState
}