package com.example.carto.search.presentation.viewmodel

sealed interface SearchSideEffect {
    data class NavigateToProduct(val productId: Long) : SearchSideEffect
}
