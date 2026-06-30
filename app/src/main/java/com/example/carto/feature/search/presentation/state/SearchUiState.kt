package com.example.carto.feature.search.presentation.state

import com.example.carto.feature.search.domain.model.SearchHistoryItem
import com.example.carto.feature.search.domain.model.SearchProduct

data class SearchUiState(
    val query: String = "",
    val history: List<SearchHistoryItem> = emptyList(),
    val products: List<SearchProduct> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val hasSearched: Boolean = false,
) {
    val canHitSearchApi: Boolean
        get() = query.trim().length > 3

    val shouldShowHistory: Boolean
        get() = query.isBlank() && history.isNotEmpty()

    val shouldShowEmptyResult: Boolean
        get() = hasSearched && !isLoading && errorMessage.isBlank() && products.isEmpty() && canHitSearchApi
}
