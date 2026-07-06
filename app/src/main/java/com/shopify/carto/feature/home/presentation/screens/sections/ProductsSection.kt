package com.shopify.carto.feature.home.presentation.screens.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shopify.carto.R
import com.shopify.carto.core.utils.ConfirmationDialog
import com.shopify.carto.feature.home.domain.model.Product
import com.shopify.carto.feature.home.presentation.screens.components.ProductCard
import com.shopify.carto.feature.home.presentation.screens.components.SectionHeader

@Composable
fun ProductsSection(
    products: List<Product>,
    isGuest: Boolean,
    favoriteIds: Set<Long>,
    onSeeAll: () -> Unit,
    onProductClick: (Product) -> Unit,
    onFavoriteClick: (Product) -> Unit,
    onGuestFavoriteClick: () -> Unit,
) {

    var productPendingRemoval by remember { mutableStateOf<Product?>(null) }

    Column {
        SectionHeader(
            title = stringResource(R.string.homeProductsTitle),
            onSeeAll = onSeeAll,
        )

        Spacer(Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(products, key = { it.id }) { product ->
                val isFavorite = favoriteIds.contains(product.id)

                ProductCard(
                    product = product,
                    modifier = Modifier.width(190.dp),
                    isGuest = isGuest,
                    isFavorite = isFavorite,
                    onClick = { onProductClick(product) },
                    onFavoriteClick = { clicked ->
                        if (isFavorite) {
                            productPendingRemoval = clicked
                        } else {
                            onFavoriteClick(clicked)
                        }
                    },
                    onGuestFavoriteClick = onGuestFavoriteClick,
                )
            }
        }
    }

    productPendingRemoval?.let { product ->
        ConfirmationDialog(
            title = stringResource(id = R.string.removeFavoriteTitle),
            message = stringResource(id = R.string.removeFavoriteMessage),
            confirmText = stringResource(id = R.string.removeFavoriteConfirm),
            cancelText = stringResource(id = R.string.removeFavoriteCancel),
            onConfirm = {
                onFavoriteClick(product)
                productPendingRemoval = null
            },
            onDismiss = { productPendingRemoval = null },
        )
    }
}