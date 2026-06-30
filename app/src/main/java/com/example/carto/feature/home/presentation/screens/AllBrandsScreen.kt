package com.example.carto.feature.home.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.carto.feature.home.presentation.HomeUiState
import com.example.carto.feature.home.presentation.HomeViewModel
import com.example.carto.feature.home.presentation.screens.components.ErrorBox
import com.example.carto.feature.home.presentation.screens.components.LoadingBox
import com.example.carto.feature.home.presentation.screens.components.BrandCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllBrandsScreen(
    viewModel: HomeViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Brands") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is HomeUiState.Loading -> LoadingBox(modifier = Modifier.fillMaxSize().padding(padding))
            is HomeUiState.Error -> ErrorBox(message = state.message, onRetry = viewModel::fetchHomeData)
            is HomeUiState.Success -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize().padding(padding)
                ) {
                    items(state.content.vendors) { vendor -> BrandCard(vendor = vendor, compact = false) }
                }
            }
        }
    }
}