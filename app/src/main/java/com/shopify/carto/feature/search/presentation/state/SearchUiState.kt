package com.shopify.carto.feature.search.presentation.state

import androidx.annotation.StringRes
import com.shopify.carto.feature.search.domain.model.SearchHistoryItem
import com.shopify.carto.feature.search.domain.model.SearchProduct

data class SearchUiState(
    val query: String = "",
    val history: List<SearchHistoryItem> = emptyList(),
    val searchProducts: List<SearchProduct> = emptyList(),
    val isSearchLoading: Boolean = false,
    @StringRes val searchErrorMessageRes: Int? = null,
    val hasSearched: Boolean = false,
) {
    val visibleHistory: List<SearchHistoryItem>
        get() = history.take(MAX_HISTORY_CHIPS)

    val isSearchMode: Boolean
        get() = query.isNotBlank()

    val shouldShowHistory: Boolean
        get() = query.isBlank() && visibleHistory.isNotEmpty()

    val shouldShowInitialPrompt: Boolean
        get() = query.isBlank()

    val shouldShowSuggestions: Boolean
        get() = query.isNotBlank() &&
            !isSearchLoading &&
            searchErrorMessageRes == null &&
            searchProducts.isNotEmpty()

    val shouldShowEmptyResult: Boolean
        get() = query.isNotBlank() &&
            hasSearched &&
            !isSearchLoading &&
            searchErrorMessageRes == null &&
            searchProducts.isEmpty()

    private companion object {
        const val MAX_HISTORY_CHIPS = 8
    }
}
