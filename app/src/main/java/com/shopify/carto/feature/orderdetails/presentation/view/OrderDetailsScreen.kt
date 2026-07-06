package com.shopify.carto.feature.orderdetails.presentation.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.shopify.carto.feature.orderdetails.presentation.state.OrderDetailsUiState
import com.shopify.carto.feature.orderdetails.presentation.util.messageRes
import com.shopify.carto.feature.orderdetails.presentation.view.components.OrderDetailsConfirmationDialog
import com.shopify.carto.feature.orderdetails.presentation.view.components.OrderDetailsErrorContent
import com.shopify.carto.feature.orderdetails.presentation.view.components.OrderDetailsSuccessContent
import com.shopify.carto.feature.orderdetails.presentation.view.components.OrderDetailsTopBar
import com.shopify.carto.feature.orderdetails.presentation.viewmodel.OrderDetailsEffect
import com.shopify.carto.feature.orderdetails.presentation.viewmodel.OrderDetailsInteractionListener
import com.shopify.carto.feature.orderdetails.presentation.viewmodel.OrderDetailsViewModel

@Composable
fun OrderDetailsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrderDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    val orderCancelledMessage = stringResource(R.string.order_details_order_cancelled_success)
    val orderRemovedMessage = stringResource(R.string.order_details_order_removed_success)

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                OrderDetailsEffect.NavigateBack -> onBackClick()
                is OrderDetailsEffect.ShowError -> snackbarHostState.showSnackbar(resources.getString(effect.type.messageRes()))
                OrderDetailsEffect.ShowOrderCancelled -> snackbarHostState.showSnackbar(orderCancelledMessage)
                OrderDetailsEffect.ShowOrderRemoved -> snackbarHostState.showSnackbar(orderRemovedMessage)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        OrderDetailsContent(
            state = state,
            interactionListener = viewModel,
            modifier = Modifier
                .padding(paddingValues),
        )
    }
}

@Composable
private fun OrderDetailsContent(
    state: OrderDetailsUiState,
    interactionListener: OrderDetailsInteractionListener,
    modifier: Modifier = Modifier,
) {
    state.pendingDialog?.let { dialog ->
        OrderDetailsConfirmationDialog(
            dialog = dialog,
            onDismiss = interactionListener::onDialogDismissed,
            onConfirm = interactionListener::onDialogConfirmed,
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
    ) {
        OrderDetailsTopBar(onBackClick = interactionListener::onBackClicked)

        AnimatedContent(
            targetState = Triple(state.isLoading, state.error, state.order),
            label = "orderDetailsContent",
            modifier = Modifier.fillMaxSize(),
        ) { (isLoading, error, order) ->
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
                    OrderDetailsErrorContent(
                        error = error,
                        onRetryClick = interactionListener::onRetryClicked,
                    )
                }

                order != null -> {
                    OrderDetailsSuccessContent(
                        order = order,
                        isProcessingAction = state.isProcessingAction,
                        onCancelOrderClick = interactionListener::onCancelOrderClicked,
                        onHideOrderClick = interactionListener::onHideOrderClicked,
                    )
                }
            }
        }
    }
}