package com.shopify.carto.feature.shopping_cart.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shopify.carto.R
import com.shopify.carto.feature.shopping_cart.domain.model.Cart
import com.shopify.carto.feature.shopping_cart.presentation.components.CartEmptyState
import com.shopify.carto.feature.shopping_cart.presentation.components.CartLineItemSection
import com.shopify.carto.feature.shopping_cart.presentation.components.CartSummarySection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateToCheckout: (String) -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CartEffect.NavigateToCheckout -> onNavigateToCheckout("")
                is CartEffect.ShowError -> snackbarHostState.showSnackbar(context.getString(effect.messageRes))
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.cartMyCart)) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                uiState.isEmpty -> CartEmptyState()
                else -> CartContent(
                    cart = requireNotNull(uiState.cart),
                    updatingLineIds = uiState.updatingLineIds,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }
}


@Composable
private fun CartContent(
    cart: Cart,
    updatingLineIds: Set<String>,
    onEvent: (CartEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var itemToDeleteId by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item { Spacer(modifier = Modifier.height(12.dp)) }

        items(cart.lines, key = { it.id }) { line ->
            CartLineItemSection(
                line = line,
                currency = cart.currency,
                isUpdating = updatingLineIds.contains(line.id),
                onIncreaseQuantity = { onEvent(CartEvent.OnIncreaseQuantity(line.id, line.quantity)) },
                onDecreaseQuantity = { onEvent(CartEvent.OnDecreaseQuantity(line.id, line.quantity)) },
                onRemove = { itemToDeleteId = line.id }
            )
        }

        item {
            CartSummarySection(
                subtotal = cart.subtotal,
                total = cart.subtotal,
                currency = cart.currency,
                onCheckoutClick = { onEvent(CartEvent.OnCheckoutClick) },
                modifier = Modifier.padding(top = 16.dp, bottom = 96.dp)
            )
        }
    }

    if (itemToDeleteId != null) {
        AlertDialog(
            onDismissRequest = { itemToDeleteId = null },
            title = { Text(text = stringResource(id = R.string.cartRemoveItem)) },
            text = { Text(text = stringResource(id = R.string.cartRemoveItemConfirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        itemToDeleteId?.let { id ->
                            onEvent(CartEvent.OnRemoveLine(id))
                        }
                        itemToDeleteId = null
                    }
                ) {
                    Text(text = stringResource(id = R.string.cartRemove))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { itemToDeleteId = null }
                ) {
                    Text(text = stringResource(id = R.string.cartCancel))
                }
            }
        )
    }
}