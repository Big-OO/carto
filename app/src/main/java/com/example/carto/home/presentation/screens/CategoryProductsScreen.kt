package com.example.carto.home.presentation.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.carto.home.presentation.CategoryProductsUiState
import com.example.carto.home.presentation.CategoryProductsViewModel
import com.example.carto.home.presentation.screens.components.CategoryChipRow
import com.example.carto.home.presentation.screens.components.LoadingBox
import com.example.carto.home.presentation.screens.components.ProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryProductsScreen(
    title: String,
    viewModel: CategoryProductsViewModel,
    onBackClick: () -> Unit,
    onProductClick: (Long) -> Unit
) {

    val state by viewModel.uiState.collectAsState()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {

            CenterAlignedTopAppBar(
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = {
                    Text(title)
                },

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

        when (val uiState = state) {

            CategoryProductsUiState.Loading -> {

                Box(
                    Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {

                  //  CircularProgressIndicator()
                    LoadingBox()

                }

            }

            is CategoryProductsUiState.Error -> {

                Box(
                    Modifier.padding(padding)
                ) {

                    Text(uiState.message)

                }

            }

            is CategoryProductsUiState.Success -> {

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {

                    CategoryChipRow(

                        chips = uiState.chips,

                        selectedChip = uiState.selectedChip,

                        onChipSelected = viewModel::selectChip

                    )

                    Spacer(Modifier.height(12.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        items(uiState.products) { product ->

                            ProductCard(

                                product = product,

                                onClick = {
                                    onProductClick(product.id)
                                }

                            )

                        }

                    }

                }

            }

        }

    }

}