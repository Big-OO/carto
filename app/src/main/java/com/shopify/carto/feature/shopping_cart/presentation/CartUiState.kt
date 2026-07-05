package com.shopify.carto.feature.shopping_cart.presentation

import com.shopify.carto.feature.shopping_cart.domain.model.Cart

data class CartUiState(
    val isLoading: Boolean = false,
    val cart: Cart? = null,
    val updatingLineIds: Set<String> = emptySet(),
    val errorMessage: String? = null
) {
    val isEmpty: Boolean get() = cart?.isEmpty != false
}