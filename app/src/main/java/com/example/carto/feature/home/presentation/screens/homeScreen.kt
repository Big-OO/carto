package com.example.carto.feature.home.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.carto.feature.home.data.HomeAdsFakeData.ads
import com.example.carto.feature.home.domain.model.Category
import com.example.carto.feature.home.domain.model.Product
import com.example.carto.feature.home.presentation.HomeContent
import com.example.carto.feature.home.presentation.HomeUiState
import com.example.carto.feature.home.presentation.HomeViewModel
import com.example.carto.feature.home.presentation.screens.components.AdsCarousel
import com.example.carto.feature.home.presentation.screens.components.ErrorBox
import com.example.carto.feature.home.presentation.screens.components.LoadingBox
import com.example.carto.feature.home.presentation.screens.sections.BrandsSection
import com.example.carto.feature.home.presentation.screens.sections.CategoriesSection
import com.example.carto.feature.home.presentation.screens.sections.HomeHeader
import com.example.carto.feature.home.presentation.screens.sections.ProductsSection
import kotlinx.coroutines.launch
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.carto.feature.favorite.presentation.FavoriteViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSeeAllProducts: () -> Unit,
    onSeeAllVendors: () -> Unit,
    onSeeAllCategories: () -> Unit,
    onProductClick: (Product) -> Unit,
    onCategoryClick: (Category) -> Unit,
    onSearchClick: () -> Unit,
    onBrandClick: (String) -> Unit,
    favoriteViewModel: FavoriteViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val session by viewModel.session.collectAsStateWithLifecycle()
    val favoriteIds by favoriteViewModel.favoriteIds.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState, Modifier.padding(bottom = 75.dp)) },
    ) { padding ->
        when (val state = uiState) {
            HomeUiState.Loading -> LoadingBox()

            is HomeUiState.Error -> ErrorBox(
                message = state.message,
                onRetry = viewModel::fetchHomeData,
            )

            is HomeUiState.Success -> HomeContent(
                modifier = Modifier.padding(padding),
                content = state.content,
                isGuest = session.isGuest,
                favoriteIds = favoriteIds,
                onSeeAllProducts = onSeeAllProducts,
                onSeeAllVendors = onSeeAllVendors,
                onSeeAllCategories = onSeeAllCategories,
                onProductClick = onProductClick,
                onCategoryClick = onCategoryClick,
                onSearchClick = onSearchClick,
                onBrandClick = onBrandClick,
                onFavoriteClick = { product ->
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

@Composable
private fun HomeContent(
    content: HomeContent,
    isGuest: Boolean,
    favoriteIds: Set<Long>,
    onSeeAllProducts: () -> Unit,
    onSeeAllVendors: () -> Unit,
    onSeeAllCategories: () -> Unit,
    onProductClick: (Product) -> Unit,
    onCategoryClick: (Category) -> Unit,
    onSearchClick: () -> Unit,
    onBrandClick: (String) -> Unit,
    onFavoriteClick: (Product) -> Unit,
    onGuestFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            HomeHeader(onSearchClick = onSearchClick)
        }

        item {
            AdsCarousel(
                ads = ads,
                onAdClick = {},
            )
        }

        item {
            CategoriesSection(
                categories = content.categories.take(6),
                onCategoryClick = onCategoryClick,
                onSeeAll = onSeeAllCategories,
            )
        }

        item {
            ProductsSection(
                products = content.products.take(6),
                isGuest = isGuest,
                favoriteIds = favoriteIds,
                onSeeAll = onSeeAllProducts,
                onProductClick = onProductClick,
                onFavoriteClick = onFavoriteClick,
                onGuestFavoriteClick = onGuestFavoriteClick,
            )
        }

        item {
            BrandsSection(
                brands = content.brands.take(6),
                onSeeAll = onSeeAllVendors,
                onBrandClick = { vendor ->
                    onBrandClick(vendor.name)
                }
            )
        }

        item {
            Spacer(Modifier.height(96.dp))
        }
    }
}
