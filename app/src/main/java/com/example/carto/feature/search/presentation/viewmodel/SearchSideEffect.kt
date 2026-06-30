package com.example.carto.feature.search.presentation.viewmodel

sealed interface SearchSideEffect {
    data class NavigateToProduct(val productId: Long) : SearchSideEffect
}
