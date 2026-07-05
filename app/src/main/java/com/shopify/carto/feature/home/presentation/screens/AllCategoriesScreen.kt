package com.shopify.carto.feature.home.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shopify.carto.feature.home.domain.model.Category
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shopify.carto.R
import com.shopify.carto.feature.home.presentation.HomeUiState
import com.shopify.carto.feature.home.presentation.HomeViewModel
import com.shopify.carto.feature.home.presentation.screens.components.CategoriesGridShimmer
import com.shopify.carto.feature.home.presentation.screens.components.CategoryCard
import com.shopify.carto.feature.home.presentation.screens.components.ErrorBox
import com.shopify.carto.feature.home.presentation.screens.components.SearchTextField


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllCategoriesScreen(
    viewModel: HomeViewModel,
    onBackClick: () -> Unit,
    onCategoryClick: (Category) -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var query by rememberSaveable {
        mutableStateOf("")
    }

    Scaffold(

        topBar = {

            CenterAlignedTopAppBar(

                title = {
                    Text(stringResource(R.string.categoriesScreenTitle))
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

        when (val state = uiState) {

            HomeUiState.Loading -> CategoriesGridShimmer(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            )

            is HomeUiState.Error -> ErrorBox(
                error = state.error,
                onRetry = viewModel::fetchHomeData,
                modifier = Modifier.padding(padding),
            )

            is HomeUiState.Success -> {

                val categories = state.content.categories

                val filteredCategories = remember(query, categories) {
                    if (query.isBlank()) {
                        categories
                    } else {
                        categories.filter {
                            it.title.contains(query, ignoreCase = true)
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                ) {

                    SearchTextField(
                        query = query,
                        onQueryChange = { query = it },
                        placeholder = stringResource(R.string.categoriesSearchHint)
                    )

                    Spacer(Modifier.height(16.dp))

                    when {

                        query.isNotBlank() && filteredCategories.isEmpty() -> {
                            EmptyCategoryView(
                                mainMsg = stringResource(R.string.categoriesEmptySearchTitle),
                                subMsg = stringResource(R.string.categoriesEmptySearchSubtitle),
                                image = Icons.Default.Category
                            )
                        }

                        query.isBlank() && filteredCategories.isEmpty() -> {
                            EmptyCategoryView(
                                mainMsg = stringResource(R.string.categoriesEmptyDataTitle),
                                subMsg = stringResource(R.string.categoriesEmptyDataSubtitle),
                                image = Icons.Default.Category
                            )
                        }

                        else -> {
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
        }
    }
}