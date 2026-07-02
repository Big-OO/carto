package com.example.carto.feature.search.presentation.state

import com.example.carto.feature.search.domain.model.SearchHistoryItem
import com.example.carto.feature.search.domain.model.SearchProduct

data class SearchUiState(
    val query: String = "",
    val history: List<SearchHistoryItem> = emptyList(),
    val initialProducts: List<SearchProduct> = emptyList(),
    val searchProducts: List<SearchProduct> = emptyList(),
    val isInitialLoading: Boolean = false,
    val isSearchLoading: Boolean = false,
    val initialErrorMessage: String = "",
    val searchErrorMessage: String = "",
    val hasLoadedInitialProducts: Boolean = false,
    val hasSearched: Boolean = false,
) {
    val visibleHistory: List<SearchHistoryItem>
        get() = history.take(MAX_HISTORY_CHIPS)

    val isSearchMode: Boolean
        get() = query.isNotBlank()

    val displayedProducts: List<SearchProduct>
        get() = if (isSearchMode) searchProducts else initialProducts

    val isLoading: Boolean
        get() = if (isSearchMode) isSearchLoading else isInitialLoading

    val errorMessage: String
        get() = if (isSearchMode) searchErrorMessage else initialErrorMessage

    val shouldShowHistory: Boolean
        get() = query.isBlank() && visibleHistory.isNotEmpty()

    val shouldShowEmptyResult: Boolean
        get() {
            val hasFinishedInitialLoading = query.isBlank() && hasLoadedInitialProducts && !isInitialLoading
            val hasFinishedSearchLoading = query.isNotBlank() && hasSearched && !isSearchLoading
            return errorMessage.isBlank() && displayedProducts.isEmpty() && (hasFinishedInitialLoading || hasFinishedSearchLoading)
        }

    private companion object {
        const val MAX_HISTORY_CHIPS = 8
    }
}
