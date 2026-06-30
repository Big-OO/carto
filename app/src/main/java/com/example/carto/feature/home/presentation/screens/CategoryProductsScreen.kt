package com.example.carto.feature.home.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.carto.feature.home.presentation.CategoryProductsUiState
import com.example.carto.feature.home.presentation.CategoryProductsViewModel
import com.example.carto.feature.home.presentation.screens.components.CategoryChipRow
import com.example.carto.feature.home.presentation.screens.components.LoadingBox
import com.example.carto.feature.home.presentation.screens.components.ProductCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryProductsScreen(
    title: String,
    viewModel: CategoryProductsViewModel,
    isGuest: Boolean,
    onBackClick: () -> Unit,
    onProductClick: (Long) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = WindowInsets(0, 0, 0, 0),
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

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(uiState.products) { product ->
                        ProductCard(
                            product = product,
                            isGuest = isGuest,
                            onClick = { onProductClick(product.id) },
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
