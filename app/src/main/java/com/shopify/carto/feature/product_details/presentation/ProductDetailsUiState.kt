package com.shopify.carto.feature.product_details.presentation

import com.shopify.carto.feature.product_details.domain.model.Product

data class ProductDetailsUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val selectedImageIndex: Int = 0,
    val selectedSize: String? = null,
    val selectedColor: String? = null,
    val isFavorite: Boolean = false,
    val isInCart: Boolean = false,
    val isAddingToCart: Boolean = false,
    val errorMessage: String? = null
) {
    val selectedVariant get() = product?.findVariant(selectedSize, selectedColor)
    val isOutOfStock get() = selectedVariant?.isAvailable == false
}