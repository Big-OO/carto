package com.shopify.carto.feature.orderhistory.presentation.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shopify.carto.R
import com.shopify.carto.feature.orderhistory.presentation.model.OrderHistoryTabUi
import com.shopify.carto.feature.orderhistory.presentation.state.OrderHistoryUiState
import com.shopify.carto.feature.orderhistory.presentation.view.components.EmptyOrders
import com.shopify.carto.feature.orderhistory.presentation.view.components.OrderHistoryCard
import com.shopify.carto.feature.orderhistory.presentation.view.components.OrderHistoryErrorContent
import com.shopify.carto.feature.orderhistory.presentation.view.components.OrderHistoryTabs
import com.shopify.carto.feature.orderhistory.presentation.view.components.OrderHistoryTopBar
import com.shopify.carto.feature.orderhistory.presentation.viewmodel.OrderHistoryEffect
import com.shopify.carto.feature.orderhistory.presentation.viewmodel.OrderHistoryInteractionListener
import com.shopify.carto.feature.orderhistory.presentation.viewmodel.OrderHistoryViewModel
import com.shopify.carto.feature.orderhistory.util.messageRes

@Composable
fun OrderHistoryScreen(
    onBackClick: () -> Unit,
    onOrderDetailsClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrderHistoryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                OrderHistoryEffect.NavigateBack -> onBackClick()
                is OrderHistoryEffect.NavigateToOrderDetails -> onOrderDetailsClick(effect.orderId)
                is OrderHistoryEffect.ShowError -> snackbarHostState.showSnackbar(resources.getString(effect.type.messageRes()))
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        OrderHistoryContent(
            state = state,
            interactionListener = viewModel,
            modifier = Modifier
                .padding(paddingValues),
        )
    }
}

@Composable
private fun OrderHistoryContent(
    state: OrderHistoryUiState,
    interactionListener: OrderHistoryInteractionListener,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OrderHistoryTopBar(onBackClick = interactionListener::onBackClicked)

        Spacer(modifier = Modifier.height(10.dp))

        OrderHistoryTabs(
            selectedTab = state.selectedTab,
            onTabClick = interactionListener::onTabClicked,
        )

        Spacer(modifier = Modifier.height(18.dp))

        AnimatedContent(
            targetState = Triple(state.isLoading, state.error, state.visibleOrders),
            label = "orderHistoryContent",
            modifier = Modifier.fillMaxSize(),
        ) { (isLoading, error, orders) ->
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                error != null -> {
                    OrderHistoryErrorContent(
                        error = error,
                        onRetryClick = interactionListener::onRetryClicked,
                    )
                }

                orders.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        EmptyOrders(
                            type = when (state.selectedTab) {
                                OrderHistoryTabUi.Ongoing -> stringResource(R.string.order_history_ongoing_tab)
                                OrderHistoryTabUi.Completed -> stringResource(R.string.order_history_completed_tab)
                            },
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 112.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(
                            items = orders,
                            key = { it.id },
                        ) { order ->
                            OrderHistoryCard(
                                order = order,
                                onSeeDetailsClick = { interactionListener.onOrderClicked(order.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}
