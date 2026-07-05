package com.shopify.carto.feature.home.presentation.screens


import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shopify.carto.feature.home.presentation.HomeUiState
import com.shopify.carto.feature.home.presentation.HomeViewModel
import com.shopify.carto.feature.home.presentation.screens.components.ErrorBox
import com.shopify.carto.feature.home.presentation.screens.components.BrandsGridShimmer
import com.shopify.carto.feature.home.presentation.screens.components.BrandCard
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.BrandingWatermark
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shopify.carto.R
import com.shopify.carto.feature.home.presentation.screens.components.SearchTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllBrandsScreen(
    viewModel: HomeViewModel,
    onBackClick: () -> Unit,
    onBrandClick: (String) -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.brandsScreenTitle)) },
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

            HomeUiState.Loading ->
                BrandsGridShimmer(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )

            is HomeUiState.Error ->
                ErrorBox(
                    error = state.error,
                    onRetry = viewModel::fetchHomeData,
                    modifier = Modifier.padding(padding),
                )

            is HomeUiState.Success -> {

                val brands = state.content.brands

                val filteredBrands = remember(
                    brands,
                    searchQuery
                ) {

                    if (searchQuery.isBlank()) {

                        brands

                    } else {

                        brands.filter {

                            it.name.contains(searchQuery, true)

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
                        onQueryChange = {
                            searchQuery = it
                        },
                        placeholder = stringResource(R.string.brandsSearchHint)
                    )

                    Spacer(Modifier.height(12.dp))

                    when {

                        searchQuery.isNotBlank() && filteredBrands.isEmpty() -> {
                            EmptyCategoryView(
                                mainMsg = stringResource(R.string.brandsEmptySearchTitle),
                                subMsg = stringResource(R.string.brandsEmptySearchSubtitle),
                                image = Icons.Default.BrandingWatermark
                            )
                        }

                        searchQuery.isBlank() && filteredBrands.isEmpty() -> {
                            EmptyCategoryView(
                                mainMsg = stringResource(R.string.brandsEmptyDataTitle),
                                subMsg = stringResource(R.string.brandsEmptyDataSubtitle),
                                image = Icons.Default.BrandingWatermark
                            )
                        }

                        else -> {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(140.dp),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(20.dp),
                                horizontalArrangement = Arrangement.spacedBy(20.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp)
                            ) {

                                items(filteredBrands) { brand ->

                                    BrandCard(
                                        brand = brand,
                                        compact = false,
                                        onBrandClick = { onBrandClick(brand.name) }
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
