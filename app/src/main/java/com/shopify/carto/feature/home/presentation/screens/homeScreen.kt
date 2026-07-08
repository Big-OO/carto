package com.shopify.carto.feature.home.presentation.screens

import android.content.ClipData
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
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shopify.carto.R
import com.shopify.carto.core.notification.util.NotificationPermissionEffect
import com.shopify.carto.feature.favorite.presentation.FavoriteViewModel
import com.shopify.carto.feature.home.domain.model.Brand
import com.shopify.carto.feature.home.domain.model.Category
import com.shopify.carto.feature.home.domain.model.Product
import com.shopify.carto.feature.home.presentation.HomeContent
import com.shopify.carto.feature.home.presentation.HomeUiState
import com.shopify.carto.feature.home.presentation.HomeViewModel
import com.shopify.carto.feature.home.presentation.screens.components.CouponsCarousel
import com.shopify.carto.feature.home.presentation.screens.components.ErrorBox
import com.shopify.carto.feature.home.presentation.screens.components.HomeScreenShimmer
import com.shopify.carto.feature.home.presentation.screens.sections.BrandsSection
import com.shopify.carto.feature.home.presentation.screens.sections.CategoriesSection
import com.shopify.carto.feature.home.presentation.screens.sections.HomeHeader
import com.shopify.carto.feature.home.presentation.screens.sections.ProductsSection
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSeeAllProducts: () -> Unit,
    onSeeAllVendors: () -> Unit,
    onSeeAllCategories: () -> Unit,
    onProductClick: (Product) -> Unit,
    onCategoryClick: (Category) -> Unit,
    onSearchClick: () -> Unit,
    onBrandClick: (Brand) -> Unit,
    favoriteViewModel: FavoriteViewModel = hiltViewModel(),
) {
    NotificationPermissionEffect()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val session by viewModel.session.collectAsStateWithLifecycle()
    val favoriteIds by favoriteViewModel.favoriteIds.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboard.current
    val guestFavoriteMessage = stringResource(R.string.commonLoginRequiredFavorite)
    val couponCopiedMessage = stringResource(R.string.homeCouponCodeCopied)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState, Modifier.padding(bottom = 75.dp)) },
    ) { padding ->
        when (val state = uiState) {
            HomeUiState.Loading -> HomeScreenShimmer(
                modifier = Modifier.padding(padding),
            )

            is HomeUiState.Error -> ErrorBox(
                error = state.error,
                onRetry = viewModel::fetchHomeData,
                modifier = Modifier.padding(padding),
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
                        snackbarHostState.showSnackbar(guestFavoriteMessage)
                    }
                },
                onCopyCouponCode = { code ->
                    val clipEntry =
                        ClipEntry(ClipData.newPlainText("coupon", AnnotatedString(code)))
                    scope.launch {
                        clipboard.setClipEntry(clipEntry)
                        snackbarHostState.showSnackbar(couponCopiedMessage)
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
    onBrandClick: (Brand) -> Unit,
    onFavoriteClick: (Product) -> Unit,
    onGuestFavoriteClick: () -> Unit,
    onCopyCouponCode: (String) -> Unit,
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

        if (content.coupons.isNotEmpty()) {
            item {
                CouponsCarousel(
                    coupons = content.coupons,
                    onCopyCodeClick = onCopyCouponCode,
                )
            }
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
                onBrandClick = onBrandClick
            )
        }

        item {
            Spacer(Modifier.height(96.dp))
        }
    }
}
