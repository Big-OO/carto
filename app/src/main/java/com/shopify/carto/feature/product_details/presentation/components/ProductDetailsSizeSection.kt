package com.shopify.carto.feature.product_details.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ProductDetailsSizeSection(
    sizes: List<String>,
    selectedSize: String?,
    onSizeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (sizes.isEmpty()) return

    Box(modifier = modifier.fillMaxWidth()) {
        LazyRow {
            items(sizes) { size ->
                SizeChip(
                    size = size,
                    isSelected = size == selectedSize,
                    onClick = { onSizeSelected(size) },
                    modifier = Modifier.padding(end = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun SizeChip(
    size: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .defaultMinSize(48.dp, minHeight = 48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color.Black else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = size,
            color = if (isSelected) Color.White else Color.Black,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}