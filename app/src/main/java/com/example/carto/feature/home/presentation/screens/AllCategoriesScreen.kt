package com.example.carto.feature.home.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.carto.feature.home.domain.model.Category
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BrandingWatermark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import com.example.carto.feature.home.presentation.screens.components.CategoryCard
import com.example.carto.feature.home.presentation.screens.components.SearchTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllCategoriesScreen(
    categories: List<Category>,
    onBackClick: () -> Unit,
    onCategoryClick: (Category) -> Unit
) {

    var query by rememberSaveable {
        mutableStateOf("")
    }

    val filteredCategories = remember(query, categories) {
        if (query.isBlank()) {
            categories
        } else {
            categories.filter {
                it.title.contains(query, ignoreCase = true)
            }
        }
    }

    Scaffold(

        topBar = {

            CenterAlignedTopAppBar(

                title = {
                    Text("Categories")
                },

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

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            SearchTextField(
                query = query,
                onQueryChange = { query = it },
                placeholder = "Search categories"
            )

            Spacer(Modifier.height(16.dp))

            if (filteredCategories.isEmpty()) {

                EmptyCategoryView(
                    mainMesg = "No categories found",
                    subMesg = "Try another keyword.",
                    Icons.Default.Category
                )

            } else {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    items(filteredCategories) {

                        CategoryCard(
                            category = it,
                            onClick = onCategoryClick
                        )

                    }
                }
            }
        }
    }
}