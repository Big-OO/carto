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
import com.example.carto.home.domain.mappers.Product
import com.example.carto.home.presentation.HomeViewModel
import com.example.carto.home.presentation.screens.components.AdsCarousel
import com.example.carto.home.presentation.screens.sections.HomeHeader
import com.example.carto.home.presentation.screens.sections.ProductsSection
import com.example.carto.home.presentation.screens.sections.BrandsSection

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSeeAllProducts: () -> Unit,
    onSeeAllVendors: () -> Unit,
    onProductClick: (Product) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color(0xFFFFFFFF)),
        contentPadding = PaddingValues(16.dp),
       verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { HomeHeader() }

        item {
            AdsCarousel(ads = ads, onAdClick = { /* future navigation */ })
        }

        item {
            ProductsSection(uiState = uiState, onSeeAll = onSeeAllProducts
                , onProductClick = onProductClick)
        }

        item {
            BrandsSection(uiState = uiState, onSeeAll = onSeeAllVendors)
        }
    }
}