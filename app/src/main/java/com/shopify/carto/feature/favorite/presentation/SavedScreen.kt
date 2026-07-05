package com.shopify.carto.feature.favorite.presentation


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shopify.carto.feature.favorite.presentation.components.FavoriteProductCard
import com.shopify.carto.feature.favorite.presentation.components.FavoriteProductCardPlaceholder
import com.shopify.carto.feature.home.presentation.screens.EmptyCategoryView
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.shopify.carto.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(
    onProductClick: (Long) -> Unit,
    viewModel: SavedViewModel = hiltViewModel(),
) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val isInitialLoading by viewModel.isInitialLoading.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(title = { Text(
                stringResource(id = R.string.savedNavTitle),
            ) })
        },
    ) { padding ->
        val isLoading = favorites.isEmpty() && isInitialLoading

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(6) {
                        FavoriteProductCardPlaceholder()
                    }
                }
            } else if (favorites.isEmpty()) {
                EmptyCategoryView(
                    mainMsg = stringResource(id = R.string.savedEmptyTitle),
                    subMsg = stringResource(id = R.string.savedEmptySubtitle),
                    image = Icons.Default.Favorite,
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(favorites, key = { it.productId }) { product ->
                        FavoriteProductCard(
                            product = product,
                            onClick = { onProductClick(it.productId) },
                            onRemoveClick = { viewModel.removeFavorite(it) },
                        )
                    }
                }
            }
        }
    }
}
