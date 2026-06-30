package com.example.carto.home.presentation.screens.sections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.carto.home.domain.model.Product
import com.example.carto.home.presentation.HomeUiState
import com.example.carto.home.presentation.screens.components.ErrorBox
import com.example.carto.home.presentation.screens.components.LoadingBox
import com.example.carto.home.presentation.screens.components.ProductCard
import com.example.carto.home.presentation.screens.components.SectionHeader


@Composable
fun ProductsSection(
    products: List<Product>,
    onSeeAll: () -> Unit,
    onProductClick: (Product) -> Unit
) {
    Column {

        SectionHeader(
            title = "All Products",
            onSeeAll = onSeeAll
        )

        Spacer(Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(products) { product ->

                ProductCard(
                    product = product,
                    modifier = Modifier.width(180.dp),
                    onClick = { onProductClick(product) },
                    onFavoriteClick = { favProduct->

                    }
                )

            }

        }

    }
}