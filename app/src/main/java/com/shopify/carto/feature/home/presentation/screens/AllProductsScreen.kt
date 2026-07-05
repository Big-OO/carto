package com.shopify.carto.feature.home.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ProductionQuantityLimits
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shopify.carto.R
import com.shopify.carto.feature.favorite.presentation.FavoriteViewModel
import com.shopify.carto.feature.home.presentation.HomeUiState
import com.shopify.carto.feature.home.presentation.HomeViewModel
import com.shopify.carto.feature.home.presentation.screens.components.ErrorBox
import com.shopify.carto.feature.home.presentation.screens.components.ProductCard
import com.shopify.carto.feature.home.presentation.screens.components.ProductsGridShimmer
import com.shopify.carto.feature.home.presentation.screens.components.SearchTextField
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllProductsScreen(
    viewModel: HomeViewModel,
    isGuest: Boolean,
    onBackClick: () -> Unit,
    onProductClick: (com.shopify.carto.feature.home.domain.model.Product) -> Unit,
    favoriteViewModel: FavoriteViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var searchQuery by rememberSaveable { mutableStateOf("") }

    val favoriteIds by favoriteViewModel.favoriteIds.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val guestFavoriteMessage = stringResource(R.string.commonLoginRequiredFavorite)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.productsScreenTitle)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->

        when (val state = uiState) {

            HomeUiState.Loading -> ProductsGridShimmer(
                modifier = Modifier.padding(padding)
            )

            is HomeUiState.Error -> ErrorBox(
                error = state.error,
                onRetry = viewModel::fetchHomeData,
                modifier = Modifier.padding(padding),
            )

            is HomeUiState.Success -> {

                val products = state.content.products

                val filteredProducts = remember(products, searchQuery) {
                    if (searchQuery.isBlank()) {
                        products
                    } else {
                        products.filter {
                            it.name.contains(searchQuery, true) ||
                                    it.vendor.contains(searchQuery, true) ||
                                    it.productType.contains(searchQuery, true)
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {

                    SearchTextField(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        placeholder = stringResource(R.string.productsSearchHint)
                    )

                    Spacer(Modifier.height(12.dp))

                    when {

                        searchQuery.isNotBlank() && filteredProducts.isEmpty() -> {
                            EmptyCategoryView(
                                mainMsg = stringResource(R.string.productsEmptySearchTitle),
                                subMsg = stringResource(R.string.productsEmptySearchSubtitle),
                                image = Icons.Default.ProductionQuantityLimits
                            )
                        }

                        searchQuery.isBlank() && filteredProducts.isEmpty() -> {
                            EmptyCategoryView(
                                mainMsg = stringResource(R.string.productsEmptyDataTitle),
                                subMsg = stringResource(R.string.productsEmptyDataSubtitle),
                                image = Icons.Default.ProductionQuantityLimits
                            )
                        }

                        else -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {

                                items(filteredProducts, key = { it.id }) { product ->

                                    ProductCard(
                                        product = product,
                                        isGuest = isGuest,
                                        isFavorite = favoriteIds.contains(product.id),
                                        onClick = onProductClick,
                                        onFavoriteClick = { clicked ->
                                            favoriteViewModel.toggleFavorite(
                                                productId = clicked.id,
                                                name = clicked.name,
                                                imageUrl = clicked.imageUrl,
                                                price = clicked.price,
                                            )
                                        },
                                        onGuestFavoriteClick = {
                                            scope.launch {
                                                snackbarHostState.showSnackbar(guestFavoriteMessage)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
