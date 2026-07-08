package com.shopify.carto.feature.product_details.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.shopify.carto.feature.product_details.presentation.ProductDetailsEvent
import com.shopify.carto.feature.product_details.presentation.ProductDetailsUiState

@Composable
fun ProductDetailsHeaderSection(
    uiState: ProductDetailsUiState,
    onEvent: (ProductDetailsEvent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        CircleIconButton(
            icon = Icons.AutoMirrored.Outlined.ArrowBack,
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        FavoriteToggleButton(
            isFavorite = uiState.isFavorite,
            onClick = { onEvent(ProductDetailsEvent.OnFavoriteClick) },
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}

@Composable
fun FavoriteToggleButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CircleIconButton(
        icon = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
        onClick = onClick,
        tint = if (isFavorite) Color.Red else Color.Black,
        modifier = modifier
    )
}

@Composable
private fun CircleIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Color.Black
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(40.dp)
            .background(MaterialTheme.colorScheme.surface, CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.padding(2.dp)
        )
    }
}