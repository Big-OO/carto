package com.shopify.carto.feature.product_details.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shopify.carto.feature.product_details.presentation.util.colorFromName

@Composable
fun ProductDetailsColorSection(
    colors: List<String>,
    selectedColor: String?,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (colors.isEmpty()) return

    Box(modifier = modifier.fillMaxWidth()) {
        LazyRow {
            items(colors) { color ->
                ColorSwatch(
                    color = color,
                    isSelected = color == selectedColor,
                    onClick = { onColorSelected(color) },
                    modifier = Modifier.padding(end = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun ColorSwatch(
    color: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.outlineVariant,
                shape = CircleShape
            )
            .padding(4.dp)
            .clip(CircleShape)
            .background(colorFromName(color))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {}
}

