package com.shopify.carto.feature.home.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shopify.carto.feature.home.presentation.CategoryProductsUiState
import com.shopify.carto.feature.home.presentation.CategoryProductsViewModel
import com.shopify.carto.feature.home.presentation.screens.components.CategoryChipRow
import com.shopify.carto.feature.home.presentation.screens.components.LoadingBox
import com.shopify.carto.core.components.ProductCard
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.filled.Category
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shopify.carto.feature.favorite.presentation.FavoriteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryProductsScreen(
    title: String,
    viewModel: CategoryProductsViewModel,
    isGuest: Boolean,
    onBackClick: () -> Unit,
    onProductClick: (Long) -> Unit,
    favoriteViewModel: FavoriteViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val favoriteIds by favoriteViewModel.favoriteIds.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
    ) { padding ->
        when (val uiState = state) {
            CategoryProductsUiState.Loading -> Box(
                Modifier
                    .padding(padding)
                    .fillMaxSize(),
            ) {
                LoadingBox()
            }

            is CategoryProductsUiState.Error -> Box(
                Modifier.padding(padding),
            ) {
                Text(uiState.message)
            }

            is CategoryProductsUiState.Success -> Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
            ) {
                CategoryChipRow(
                    chips = uiState.chips,
                    selectedChip = uiState.selectedChip,
                    onChipSelected = viewModel::selectChip,
                )

                Spacer(Modifier.height(12.dp))
                if (uiState.products.isEmpty()) {

                    EmptyCategoryView(
                        mainMsg = "No products found",
                        subMsg = "This category doesn't contain any products yet.",
                        image = Icons.Default.Category
                    )

                } else {

                  LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                      items(uiState.products, key = { it.id }) { product ->
                          ProductCard(
                              name = product.name,
                              price = product.price,
                              imageUrl = product.imageUrl,
                              compareAtPrice = product.compareAtPrice,
                              isNew = product.isNew,
                              isOnSale = product.isOnSale,
                              productType = product.productType,
                              imageCount = product.imageCount,
                              isGuest = isGuest,
                              isFavorite = favoriteIds.contains(product.id),
                              onClick = { onProductClick(product.id) },
                              onFavoriteClick = {
                                  favoriteViewModel.toggleFavorite(
                                      productId = product.id,
                                      name = product.name,
                                      imageUrl = product.imageUrl,
                                      price = product.price,
                                  )
                              },
                              onGuestFavoriteClick = {
                                  scope.launch {
                                      snackbarHostState.showSnackbar("Please login to add favorites.")
                                  }
                              },
                          )
                      }
                }
                }
            }
        }
    }
}

@Composable
fun EmptyCategoryView(mainMsg:String, subMsg:String, image: ImageVector?) {

    val infiniteTransition = rememberInfiniteTransition(label = "floating")

    val offsetY by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1800,
                easing = EaseInOut
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconOffset"
    )

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(
            animationSpec = tween(500)
        ) + slideInVertically(
            initialOffsetY = { it / 3 },
            animationSpec = tween(500)
        )
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Icon(
                    imageVector = image?: Icons.Default.Inventory2,
                    contentDescription = null,
                    modifier = Modifier
                        .size(72.dp)
                        .offset(y = offsetY.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    text = mainMsg,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = subMsg,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}