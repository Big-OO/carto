package com.example.carto.home.presentation.screens.sections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.carto.home.presentation.HomeUiState
import com.example.carto.home.presentation.screens.components.ErrorBox
import com.example.carto.home.presentation.screens.components.LoadingBox
import com.example.carto.home.presentation.screens.components.SectionHeader
import com.example.carto.home.presentation.screens.components.BrandCard
import androidx.compose.runtime.Composable

private const val HOME_PREVIEW_VENDOR_LIMIT = 8

@Composable
fun BrandsSection(
    uiState: HomeUiState,
    onSeeAll: () -> Unit
) {
    Column {
        SectionHeader(title = "All Brands", onSeeAll = onSeeAll)
        Spacer(Modifier.height(8.dp))

        when (uiState) {
            is HomeUiState.Loading -> LoadingBox()
            is HomeUiState.Error -> ErrorBox(message = uiState.message, onRetry = {})
            is HomeUiState.Success -> {
                val previewVendors = uiState.vendors.take(HOME_PREVIEW_VENDOR_LIMIT)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(previewVendors) { vendor ->
                        BrandCard(vendor = vendor, compact = true)
                    }
                }
            }
        }
    }
}
