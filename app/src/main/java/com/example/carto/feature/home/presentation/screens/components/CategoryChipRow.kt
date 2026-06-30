package com.example.carto.feature.home.presentation.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CategoryChipRow(
    chips: List<String>,
    selectedChip: String,
    onChipSelected: (String) -> Unit
) {

    LazyRow(

        modifier = Modifier.padding(vertical = 8.dp),

        contentPadding = PaddingValues(horizontal = 16.dp),

        horizontalArrangement = Arrangement.spacedBy(8.dp)

    ) {

        items(chips) { chip ->

            FilterChip(
                shape = RoundedCornerShape(12.dp),
                selected = chip == selectedChip,

                onClick = {
                    onChipSelected(chip)
                },

                label = {
                    Text(chip)
                },

                colors = FilterChipDefaults.filterChipColors(

                    selectedContainerColor = MaterialTheme.colorScheme.primary,

                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,

                    containerColor = MaterialTheme.colorScheme.surface,

                    labelColor = MaterialTheme.colorScheme.primary

                )

            )

        }

    }

}