package com.shopify.carto.feature.search.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopify.carto.R
import com.shopify.carto.feature.search.domain.model.SearchFailure
import com.shopify.carto.feature.search.domain.model.SearchFailureType
import com.shopify.carto.feature.search.domain.model.SearchResult
import com.shopify.carto.feature.search.domain.usecases.ClearSearchHistoryUseCase
import com.shopify.carto.feature.search.domain.usecases.DeleteSearchHistoryItemUseCase
import com.shopify.carto.feature.search.domain.usecases.ObserveSearchHistoryUseCase
import com.shopify.carto.feature.search.domain.usecases.SaveSearchQueryUseCase
import com.shopify.carto.feature.search.domain.usecases.SearchProductsUseCase
import com.shopify.carto.feature.search.presentation.state.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchProductsUseCase: SearchProductsUseCase,
    private val observeSearchHistoryUseCase: ObserveSearchHistoryUseCase,
    private val saveSearchQueryUseCase: SaveSearchQueryUseCase,
    private val deleteSearchHistoryItemUseCase: DeleteSearchHistoryItemUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase,
) : ViewModel(), SearchInteractionListener {
    private val _state = MutableStateFlow(SearchUiState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<SearchSideEffect>()
    val effects = _effects.asSharedFlow()

    init {
        observeHistory()
        observeQueryChanges()
    }

    override fun onSearchValueChanged(newValue: String) {
        val cleanedValue = newValue.trimStart()
        _state.update {
            it.copy(
                query = cleanedValue,
                searchErrorMessageRes = null,
                searchProducts = emptyList(),
                hasSearched = false,
            )
        }
    }

    override fun onSearchSubmitted() {
        val query = _state.value.query.trim()
        if (query.isBlank()) return

        viewModelScope.launch {
            saveQuery(query)
            searchProducts(query)
        }
    }

    override fun onClearSearchClicked() {
        _state.update {
            it.copy(
                query = "",
                searchProducts = emptyList(),
                isSearchLoading = false,
                searchErrorMessageRes = null,
                hasSearched = false,
            )
        }
    }

    override fun onHistoryItemClicked(query: String) {
        _state.update {
            it.copy(
                query = query,
                searchProducts = emptyList(),
                searchErrorMessageRes = null,
                hasSearched = false,
            )
        }

        viewModelScope.launch {
            saveQuery(query)
        }
    }

    override fun onHistoryItemDeleted(id: Long) {
        viewModelScope.launch {
            when (val result = deleteSearchHistoryItemUseCase(id)) {
                is SearchResult.Success -> Unit
                is SearchResult.Failure -> result.failure
            }
        }
    }

    override fun onClearHistoryClicked() {
        viewModelScope.launch {
            when (val result = clearSearchHistoryUseCase()) {
                is SearchResult.Success -> Unit
                is SearchResult.Failure -> result.failure
            }
        }
    }

    override fun onProductClicked(productId: Long) {
        val query = _state.value.query.trim()
        viewModelScope.launch {
            if (query.isNotBlank()) {
                saveQuery(query)
            }
            _effects.emit(SearchSideEffect.NavigateToProduct(productId))
        }
    }

    override fun onProductSuggestionClicked(productId: Long, productTitle: String) {
        val cleanedTitle = productTitle.trim()
        viewModelScope.launch {
            if (cleanedTitle.isNotBlank()) {
                saveQuery(cleanedTitle)
            }
            _effects.emit(SearchSideEffect.NavigateToProduct(productId))
        }
    }

    override fun onBackClicked() {
        viewModelScope.launch {
            _effects.emit(SearchSideEffect.NavigateBack)
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            observeSearchHistoryUseCase().collect { history ->
                _state.update { it.copy(history = history) }
            }
        }
    }

    private fun observeQueryChanges() {
        viewModelScope.launch {
            state
                .map { it.query.trim() }
                .debounce(SEARCH_DEBOUNCE_MILLIS)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isBlank()) return@collectLatest
                    searchProducts(query)
                }
        }
    }

    private suspend fun searchProducts(query: String) {
        _state.update {
            it.copy(
                isSearchLoading = true,
                searchErrorMessageRes = null,
                hasSearched = true,
            )
        }

        when (val result = searchProductsUseCase(query)) {
            is SearchResult.Success -> {
                _state.update {
                    it.copy(
                        isSearchLoading = false,
                        searchProducts = result.data,
                        searchErrorMessageRes = null,
                        hasSearched = true,
                    )
                }
            }

            is SearchResult.Failure -> {
                result.failure
                _state.update {
                    it.copy(
                        isSearchLoading = false,
                        searchProducts = emptyList(),
                        searchErrorMessageRes = result.failure.toUserMessageRes(),
                        hasSearched = true,
                    )
                }
            }
        }
    }

    private suspend fun saveQuery(query: String) {
        when (val result = saveSearchQueryUseCase(query)) {
            is SearchResult.Success -> Unit
            is SearchResult.Failure -> result.failure
        }
    }

    private fun SearchFailure.toUserMessageRes(): Int {
        return when (type) {
            SearchFailureType.Network -> R.string.search_error_network
            SearchFailureType.Unauthorized -> R.string.search_error_unauthorized
            SearchFailureType.Server -> R.string.search_error_server
            SearchFailureType.ShopifyConfigurationMissing,
            SearchFailureType.LocalStorage,
            SearchFailureType.Unknown -> R.string.search_error_unknown
        }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MILLIS = 500L
    }
}
