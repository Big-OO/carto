package com.shopify.carto.feature.search.presentation.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.shopify.carto.feature.search.presentation.state.SearchUiState
import com.shopify.carto.feature.search.presentation.view.components.EmptySearchContent
import com.shopify.carto.feature.search.presentation.view.components.SearchHistorySection
import com.shopify.carto.feature.search.presentation.view.components.SearchInputBar
import com.shopify.carto.feature.search.presentation.view.components.SearchProductResultItem
import com.shopify.carto.feature.search.presentation.view.components.SearchProductResultItemShimmer
import com.shopify.carto.feature.search.presentation.view.components.SearchTopBar
import com.shopify.carto.feature.search.presentation.viewmodel.SearchInteractionListener
import com.shopify.carto.feature.search.presentation.viewmodel.SearchSideEffect
import com.shopify.carto.feature.search.presentation.viewmodel.SearchViewModel

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
                SearchSideEffect.NavigateBack -> onBackClick()
            }
        }
    }

    SearchScreenContent(
        state = state,
        interactionListener = viewModel,
    )
}

@Composable
private fun SearchScreenContent(
    state: SearchUiState,
    interactionListener: SearchInteractionListener,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 50.dp),
    ) {
        SearchTopBar(
            onBackClick = interactionListener::onBackClicked,
        )

        Spacer(Modifier.height(34.dp))

        SearchInputBar(
            query = state.query,
            onQueryChanged = interactionListener::onSearchValueChanged,
            onSearchSubmitted = interactionListener::onSearchSubmitted,
        )

        Spacer(Modifier.height(26.dp))

        SearchContent(
            state = state,
            interactionListener = interactionListener,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SearchContent(
    state: SearchUiState,
    interactionListener: SearchInteractionListener,
    modifier: Modifier = Modifier,
) {
    when {
        state.errorMessage.isNotBlank() -> {
            Box(modifier = modifier.fillMaxSize()) {
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
        }

        state.shouldShowEmptyResult -> {
            Box(modifier = modifier.fillMaxSize()) {
                EmptySearchContent(
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }

        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
            ) {
                if (state.shouldShowHistory) {
                    item(key = "history") {
                        SearchHistorySection(
                            history = state.visibleHistory,
                            onHistoryItemClicked = interactionListener::onHistoryItemClicked,
                            onHistoryItemDeleted = interactionListener::onHistoryItemDeleted,
                            onClearHistoryClicked = interactionListener::onClearHistoryClicked,
                        )

                        Spacer(Modifier.height(24.dp))
                    }
                }

                if (state.isLoading) {
                    items(SHIMMER_ITEM_COUNT) { index ->
                        SearchProductResultItemShimmer(
                            showDivider = index != SHIMMER_ITEM_COUNT - 1,
                        )
                    }
                } else {
                    itemsIndexed(
                        items = state.displayedProducts,
                        key = { _, product -> product.id },
                    ) { index, product ->
                        SearchProductResultItem(
                            product = product,
                            onClick = { interactionListener.onProductClicked(product.id) },
                            showDivider = index != state.displayedProducts.lastIndex,
                        )
                    }
                }
            }
        }
    }
}

private const val SHIMMER_ITEM_COUNT = 5
