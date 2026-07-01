package com.example.carto.feature.brand.presentation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.carto.feature.brand.presentation.components.BrandHeader
import com.example.carto.feature.brand.presentation.components.FilterSection
import com.example.carto.feature.brand.presentation.components.ProductCard
import androidx.compose.foundation.BorderStroke
import com.example.carto.core.utils.shimmerEffect
import com.example.carto.feature.brand.presentation.components.BrandShimmerLoading
import com.example.carto.feature.home.presentation.screens.components.ErrorBox

@Immutable
data class Product(
    val id: String,
    val name: String,
    val type: String,
    val price: String,
    val imageUrl: String
)

@Immutable
data class Brand(val name: String, val imageUrl: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandScreen(
    brandId: String,
    onBackClick: () -> Unit,
    onProductClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BrandViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(brandId) {
        viewModel.onEvent(BrandEvent.LoadBrand(brandId))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                BrandEffect.NavigateBack -> onBackClick()
                is BrandEffect.NavigateToProductDetail -> onProductClick(effect.productId)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = brandId) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(BrandEvent.ClickBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val currentState = state) {
                BrandUiState.Loading -> {
                    BrandShimmerLoading(modifier = Modifier.fillMaxSize())
                }

                is BrandUiState.Error -> {
                    ErrorBox(
                        message = currentState.message,
                        onRetry = { viewModel.onEvent(BrandEvent.LoadBrand(brandId)) }
                    )
                }

                is BrandUiState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 120.dp),
                        state = rememberLazyGridState(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item(
                            span = { GridItemSpan(maxLineSpan) }
                        ) {
                            BrandHeader(brand = currentState.brand)
                        }

                        item(
                            span = { GridItemSpan(maxLineSpan) }
                        ) {
                            FilterSection(
                                types = currentState.filterChips,
                                selectedType = currentState.selectedChip,
                                onTypeSelected = { type ->
                                    viewModel.onEvent(BrandEvent.FilterProductType(type))
                                }
                            )
                        }

                        items(
                            items = currentState.filteredProducts,
                            key = { it.id },
                            contentType = { "product" }
                        ) { product ->
                            ProductCard(
                                product = product,
                                modifier = Modifier.clickable {
                                    viewModel.onEvent(BrandEvent.ClickProduct(product.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}