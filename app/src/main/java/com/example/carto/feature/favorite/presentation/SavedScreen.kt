package com.example.carto.feature.favorite.presentation


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
import com.example.carto.feature.favorite.presentation.components.FavoriteProductCard
import com.example.carto.feature.favorite.presentation.components.FavoriteProductCardPlaceholder
import com.example.carto.feature.home.presentation.screens.EmptyCategoryView
import androidx.compose.foundation.lazy.grid.GridItemSpan


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
            CenterAlignedTopAppBar(title = { Text("Saved") })
        },
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val isLoading = favorites.isEmpty() && isInitialLoading

            if (isLoading) {
                items(6) {
                    FavoriteProductCardPlaceholder()
                }
            } else if (favorites.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EmptyCategoryView(
                        mainMsg = "No favorites yet",
                        subMsg = "Tap the heart on any product to save it here.",
                        image = Icons.Default.Favorite,
                    )
                }
            } else {
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
