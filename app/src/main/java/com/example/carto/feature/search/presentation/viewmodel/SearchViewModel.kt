package com.example.carto.feature.search.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carto.feature.search.domain.model.SearchFailure
import com.example.carto.feature.search.domain.model.SearchFailureType
import com.example.carto.feature.search.domain.model.SearchResult
import com.example.carto.feature.search.domain.usecases.ClearSearchHistoryUseCase
import com.example.carto.feature.search.domain.usecases.DeleteSearchHistoryItemUseCase
import com.example.carto.feature.search.domain.usecases.GetInitialSearchProductsUseCase
import com.example.carto.feature.search.domain.usecases.ObserveSearchHistoryUseCase
import com.example.carto.feature.search.domain.usecases.SaveSearchQueryUseCase
import com.example.carto.feature.search.domain.usecases.SearchProductsUseCase
import com.example.carto.feature.search.presentation.state.SearchUiState
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
    private val getInitialSearchProductsUseCase: GetInitialSearchProductsUseCase,
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
        loadInitialProducts()
        observeQueryChanges()
    }

    override fun onSearchValueChanged(newValue: String) {
        val cleanedValue = newValue.trimStart()
        _state.update {
            it.copy(
                query = cleanedValue,
                searchErrorMessage = "",
                searchProducts = if (cleanedValue.isBlank()) emptyList() else it.searchProducts,
                hasSearched = if (cleanedValue.isBlank()) false else it.hasSearched,
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

    override fun onHistoryItemClicked(query: String) {
        _state.update {
            it.copy(
                query = query,
                searchProducts = emptyList(),
                searchErrorMessage = "",
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
                is SearchResult.Failure -> result.failure.logForDeveloper()
            }
        }
    }

    override fun onClearHistoryClicked() {
        viewModelScope.launch {
            when (val result = clearSearchHistoryUseCase()) {
                is SearchResult.Success -> Unit
                is SearchResult.Failure -> result.failure.logForDeveloper()
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

    private fun loadInitialProducts() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isInitialLoading = true,
                    initialErrorMessage = "",
                )
            }

            when (val result = getInitialSearchProductsUseCase()) {
                is SearchResult.Success -> {
                    _state.update {
                        it.copy(
                            isInitialLoading = false,
                            initialProducts = result.data,
                            initialErrorMessage = "",
                            hasLoadedInitialProducts = true,
                        )
                    }
                }

                is SearchResult.Failure -> {
                    result.failure.logForDeveloper()
                    _state.update {
                        it.copy(
                            isInitialLoading = false,
                            initialProducts = emptyList(),
                            initialErrorMessage = result.failure.toUserMessage(),
                            hasLoadedInitialProducts = true,
                        )
                    }
                }
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
                searchErrorMessage = "",
                hasSearched = true,
            )
        }

        when (val result = searchProductsUseCase(query)) {
            is SearchResult.Success -> {
                _state.update {
                    it.copy(
                        isSearchLoading = false,
                        searchProducts = result.data,
                        searchErrorMessage = "",
                        hasSearched = true,
                    )
                }
            }

            is SearchResult.Failure -> {
                result.failure.logForDeveloper()
                _state.update {
                    it.copy(
                        isSearchLoading = false,
                        searchProducts = emptyList(),
                        searchErrorMessage = result.failure.toUserMessage(),
                        hasSearched = true,
                    )
                }
            }
        }
    }

    private suspend fun saveQuery(query: String) {
        when (val result = saveSearchQueryUseCase(query)) {
            is SearchResult.Success -> Unit
            is SearchResult.Failure -> result.failure.logForDeveloper()
        }
    }

    private fun SearchFailure.toUserMessage(): String {
        return when (type) {
            SearchFailureType.Network -> "Check your internet connection and try again."
            SearchFailureType.Unauthorized -> "We couldn't load products right now. Try again later."
            SearchFailureType.Server -> "The store is busy right now. Try again later."
            SearchFailureType.ShopifyConfigurationMissing,
            SearchFailureType.LocalStorage,
            SearchFailureType.Unknown -> "Something went wrong. Try again later."
        }
    }

    private fun SearchFailure.logForDeveloper() {
        Log.e(TAG, "Search failed. type=$type, details=$developerMessage")
    }

    private companion object {
        const val TAG = "SearchViewModel"
        const val SEARCH_DEBOUNCE_MILLIS = 500L
    }
}
