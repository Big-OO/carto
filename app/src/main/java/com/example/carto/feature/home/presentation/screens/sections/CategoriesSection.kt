package com.example.carto.feature.home.presentation.screens.sections


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.carto.feature.home.domain.model.Category
import com.example.carto.feature.home.presentation.screens.components.CategoryCard
import com.example.carto.feature.home.presentation.screens.components.SectionHeader

@Composable
fun CategoriesSection(
    categories: List<Category>,
    onCategoryClick: (Category) -> Unit
) {

    Column {

        Text(
            text = "Categories",
            modifier = androidx.compose.ui.Modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

            items(categories) { category ->

                CategoryCard(
                    category = category,
                    onClick = onCategoryClick
                )

            }

        }

    }

}