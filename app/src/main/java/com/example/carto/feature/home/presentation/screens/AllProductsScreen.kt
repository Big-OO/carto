package com.example.carto.feature.home.presentation.screens

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
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.PestControlRodent
import androidx.compose.material.icons.filled.ProductionQuantityLimits
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.carto.feature.home.domain.model.Product
import com.example.carto.feature.home.presentation.screens.components.ProductCard
import com.example.carto.feature.home.presentation.screens.components.SearchTextField
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllProductsScreen(
    products: List<Product>,
    isGuest: Boolean,
    onBackClick: () -> Unit,
    onProductClick: (Product) -> Unit,
) {

    var searchQuery by rememberSaveable { mutableStateOf("") }

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

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Products") },
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            SearchTextField(
                modifier = Modifier.padding(horizontal = 16.dp),
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search products..."
            )

            Spacer(Modifier.height(12.dp))

            if (filteredProducts.isEmpty()) {

                EmptyCategoryView(
                    mainMesg = "No products found",
                    subMesg = "Try another keyword.",
                    image = Icons.Default.ProductionQuantityLimits
                )

            } else {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(filteredProducts) { product ->

                        ProductCard(
                            product = product,
                            isGuest = isGuest,
                            onClick = onProductClick,
                            onGuestFavoriteClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Please login to add favorites."
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}