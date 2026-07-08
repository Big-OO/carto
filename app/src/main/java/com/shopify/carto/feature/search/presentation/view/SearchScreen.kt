package com.shopify.carto.feature.search.presentation.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shopify.carto.feature.search.presentation.state.SearchUiState
import com.shopify.carto.feature.search.presentation.view.components.EmptySearchContent
import com.shopify.carto.feature.search.presentation.view.components.InitialSearchContent
import com.shopify.carto.feature.search.presentation.view.components.SearchHistorySection
import com.shopify.carto.feature.search.presentation.view.components.SearchInputBar
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
            isSearching = state.isSearchLoading,
            suggestions = state.searchProducts,
            showSuggestions = state.shouldShowSuggestions,
            onQueryChanged = interactionListener::onSearchValueChanged,
            onClearClick = interactionListener::onClearSearchClicked,
            onSearchSubmitted = interactionListener::onSearchSubmitted,
            onSuggestionClick = { product ->
                interactionListener.onProductSuggestionClicked(product.id, product.title)
            },
        )

        Spacer(Modifier.height(26.dp))

        SearchContent(
            state = state,
            interactionListener = interactionListener,
            modifier = Modifier.weight(1f),
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun SearchContent(
    state: SearchUiState,
    interactionListener: SearchInteractionListener,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = state.isSearchMode,
        transitionSpec = {
            (fadeIn() + scaleIn(initialScale = 0.96f)) togetherWith
                (fadeOut() + scaleOut(targetScale = 0.96f))
        },
        label = "search-content-mode",
        modifier = modifier.fillMaxSize(),
    ) { isSearchMode ->
        if (!isSearchMode) {
            InitialSearchModeContent(
                state = state,
                interactionListener = interactionListener,
            )
        } else {
            SearchModeMessageContent(
                state = state,
            )
        }
    }
}

@Composable
private fun InitialSearchModeContent(
    state: SearchUiState,
    interactionListener: SearchInteractionListener,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        if (state.shouldShowHistory) {
            SearchHistorySection(
                history = state.visibleHistory,
                onHistoryItemClicked = interactionListener::onHistoryItemClicked,
                onHistoryItemDeleted = interactionListener::onHistoryItemDeleted,
            )
        }

        InitialSearchContent(
            modifier = Modifier.padding(horizontal = 24.dp).align(Alignment.Center),
        )
    }
}

@Composable
private fun SearchModeMessageContent(
    state: SearchUiState,
    modifier: Modifier = Modifier,
) {
    when {
        state.searchErrorMessageRes != null -> {
            Box(modifier = modifier.fillMaxSize()) {
                Text(
                    text = stringResource(state.searchErrorMessageRes),
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

        else -> Unit
    }
}
