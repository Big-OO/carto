package com.example.carto.feature.home.presentation.screens.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.carto.feature.home.domain.model.Product
import com.example.carto.feature.home.presentation.screens.components.ProductCard
import com.example.carto.feature.home.presentation.screens.components.SectionHeader

@Composable
fun ProductsSection(
    products: List<Product>,
    isGuest: Boolean,
    onSeeAll: () -> Unit,
    onProductClick: (Product) -> Unit,
    onGuestFavoriteClick: () -> Unit,
) {
    Column {
        SectionHeader(
            title = "All Products",
            onSeeAll = onSeeAll,
        )

        Spacer(Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    modifier = Modifier.width(190.dp),
                    isGuest = isGuest,
                    onClick = { onProductClick(product) },
                    onGuestFavoriteClick = onGuestFavoriteClick
                )
            }
        }
    }
}
