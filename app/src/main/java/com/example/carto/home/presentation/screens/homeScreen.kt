package com.example.carto.home.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.carto.home.data.HomeAdsFakeData.ads
import com.example.carto.home.domain.model.Category
import com.example.carto.home.domain.model.Product
import com.example.carto.home.presentation.HomeContent
import com.example.carto.home.presentation.HomeUiState
import com.example.carto.home.presentation.HomeViewModel
import com.example.carto.home.presentation.screens.components.AdsCarousel
import com.example.carto.home.presentation.screens.components.ErrorBox
import com.example.carto.home.presentation.screens.components.LoadingBox
import com.example.carto.home.presentation.screens.sections.HomeHeader
import com.example.carto.home.presentation.screens.sections.ProductsSection
import com.example.carto.home.presentation.screens.sections.BrandsSection
import com.example.carto.home.presentation.screens.sections.CategoriesSection

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSeeAllProducts: () -> Unit,
    onSeeAllVendors: () -> Unit,
    onProductClick: (Product) -> Unit,
    onCategoryClick: (Category) -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {

        HomeUiState.Loading -> {
            LoadingBox()
        }

        is HomeUiState.Error -> {
            ErrorBox(
                message = state.message,
                onRetry = viewModel::fetchHomeData
            )
        }

        is HomeUiState.Success -> {

            HomeContent(
                content = state.content,
                onSeeAllProducts = onSeeAllProducts,
                onSeeAllVendors = onSeeAllVendors,
                onProductClick = onProductClick,
                onCategoryClick = onCategoryClick
            )

        }
    }
}

@Composable
private fun HomeContent(
    content: HomeContent,
    onSeeAllProducts: () -> Unit,
    onSeeAllVendors: () -> Unit,
    onProductClick: (Product) -> Unit,
    onCategoryClick: (Category) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            HomeHeader()
        }

        item {
            AdsCarousel(
                ads = ads,
                onAdClick = {}
            )
        }

        item {

            CategoriesSection(
                categories = content.categories,
                onCategoryClick = onCategoryClick
            )

        }

        item {

            ProductsSection(
                products = content.products,
                onSeeAll = onSeeAllProducts,
                onProductClick = onProductClick
            )

        }

        item {

            BrandsSection(
                vendors = content.vendors,
                onSeeAll = onSeeAllVendors
            )

        }

    }

}