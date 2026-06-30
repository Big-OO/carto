package com.example.carto.feature.search.presentation.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.carto.feature.search.presentation.state.SearchUiState
import com.example.carto.feature.search.presentation.view.components.EmptySearchContent
import com.example.carto.feature.search.presentation.view.components.SearchHistorySection
import com.example.carto.feature.search.presentation.view.components.SearchInputBar
import com.example.carto.feature.search.presentation.view.components.SearchProductResultItem
import com.example.carto.feature.search.presentation.view.components.SearchTopBar
import com.example.carto.feature.search.presentation.viewmodel.SearchInteractionListener
import com.example.carto.feature.search.presentation.viewmodel.SearchSideEffect
import com.example.carto.feature.search.presentation.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    onBackClick: () -> Unit = {},
    onProductClick: (Long) -> Unit = {},
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is SearchSideEffect.NavigateToProduct -> onProductClick(effect.productId)
            }
        }
    }

    SearchScreenContent(
        state = state,
        interactionListener = viewModel,
        onBackClick = onBackClick,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SearchScreenContent(
    state: SearchUiState,
    interactionListener: SearchInteractionListener,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 50.dp),
    ) {
        SearchTopBar(
            onBackClick = {
                interactionListener.onBackClicked()
                onBackClick()
            },
        )

        Spacer(Modifier.height(34.dp))

        SearchInputBar(
            query = state.query,
            onQueryChanged = interactionListener::onSearchValueChanged,
            onSearchSubmitted = interactionListener::onSearchSubmitted,
        )

        Spacer(Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            when {
                state.shouldShowHistory -> {
                    SearchHistorySection(
                        history = state.history,
                        onHistoryItemClicked = interactionListener::onHistoryItemClicked,
                        onHistoryItemDeleted = interactionListener::onHistoryItemDeleted,
                        onClearHistoryClicked = interactionListener::onClearHistoryClicked,
                    )
                }

                state.isLoading -> {
                    CircularWavyProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.errorMessage.isNotBlank() -> {
                    Text(
                        text = state.errorMessage,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 24.dp),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                    )
                }

                state.shouldShowEmptyResult -> {
                    EmptySearchContent(
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                state.products.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        itemsIndexed(
                            items = state.products,
                            key = { _, product -> product.id },
                        ) { index, product ->
                            SearchProductResultItem(
                                product = product,
                                onClick = { interactionListener.onProductClicked(product.id) },
                                showDivider = index != state.products.lastIndex,
                            )
                        }
                    }
                }
            }
        }
    }
}
