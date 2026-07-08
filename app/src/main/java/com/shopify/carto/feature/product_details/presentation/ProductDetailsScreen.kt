package com.shopify.carto.feature.product_details.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shopify.carto.R
import com.shopify.carto.feature.product_details.presentation.components.ProductDetailsColorSection
import com.shopify.carto.feature.product_details.presentation.components.ProductDetailsDescriptionSection
import com.shopify.carto.feature.product_details.presentation.components.ProductDetailsHeaderSection
import com.shopify.carto.feature.product_details.presentation.components.ProductDetailsImageSection
import com.shopify.carto.feature.product_details.presentation.components.ProductDetailsPriceSection
import com.shopify.carto.feature.product_details.presentation.components.ProductDetailsSizeSection

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    productId: String,
    onBackClick: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToReviews: (String) -> Unit,
    viewModel: ProductDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(productId) {
        viewModel.loadProductDetails(productId)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ProductDetailsEffect.NavigateBack -> onBackClick()
                ProductDetailsEffect.NavigateToCart -> onNavigateToCart()
                is ProductDetailsEffect.ShowError -> snackbarHostState.showSnackbar(
                    context.getString(
                        effect.messageRes
                    )
                )
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 24.dp)
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> LoadingState()
                uiState.errorMessage != null -> ErrorState(
                    message = uiState.errorMessage.orEmpty(),
                    onRetryClick = { viewModel.onEvent(ProductDetailsEvent.OnRetryClick) }
                )

                uiState.product != null -> ProductDetailsContent(
                    uiState = uiState,
                    onEvent = viewModel::onEvent,
                    onReviewsClick = { onNavigateToReviews(productId) }
                )
            }

            ProductDetailsHeaderSection(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onBackClick = { viewModel.onEvent(ProductDetailsEvent.OnBackClick) },
                modifier = Modifier.safeContentPadding()
            )
        }
    }
}

@Composable
private fun ProductDetailsContent(
    uiState: ProductDetailsUiState,
    onEvent: (ProductDetailsEvent) -> Unit,
    onReviewsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val product = requireNotNull(uiState.product)

    var showRemoveDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                ProductDetailsImageSection(
                    images = product.images,
                    selectedIndex = uiState.selectedImageIndex,
                    onImageSelected = { onEvent(ProductDetailsEvent.OnImageSelected(it)) }
                )
            }

            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ProductDetailsDescriptionSection(
                    title = product.title,
                    description = product.description
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onReviewsClick() }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "4.1/5",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(45 reviews)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                ProductDetailsSizeSection(
                    sizes = product.sizes,
                    selectedSize = uiState.selectedSize,
                    onSizeSelected = { onEvent(ProductDetailsEvent.OnSizeSelected(it)) }
                )

                ProductDetailsColorSection(
                    colors = product.colors,
                    selectedColor = uiState.selectedColor,
                    onColorSelected = { onEvent(ProductDetailsEvent.OnColorSelected(it)) }
                )
            }
        }

        ProductDetailsPriceSection(
            price = product.price,
            currency = product.currency,
            isInCart = uiState.isInCart,
            isOutOfStock = uiState.isOutOfStock,
            isAddingToCart = uiState.isAddingToCart,
            onButtonClick = {
                if (uiState.isInCart) {
                    showRemoveDialog = true
                } else {
                    onEvent(ProductDetailsEvent.OnAddToCartClick)
                }
            },
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )
    }
    val removeTitle = stringResource(R.string.productDetailsRemoveFromCart)
    val removeMessage = stringResource(R.string.productDetailsRemoveFromCartConfirmation)
    val removeButton = stringResource(R.string.cartRemove)
    val cancelButton = stringResource(R.string.cartCancel)

    if (showRemoveDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveDialog = false },
            title = {
                Text(removeTitle)
            },
            text = {
                Text(removeMessage)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEvent(ProductDetailsEvent.OnRemoveFromCartConfirm)
                        showRemoveDialog = false
                    }
                ) {
                    Text(removeButton)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRemoveDialog = false }
                ) {
                    Text(cancelButton)
                }
            }
        )
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            TextButton(onClick = onRetryClick) {
                Text(text = stringResource(id = R.string.productDetailsTryAgain))
            }
        }
    }
}