package com.shopify.carto.feature.ai_integration.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shopify.carto.core.components.ProductCard
import com.shopify.carto.feature.search.domain.model.SearchProduct
import com.shopify.carto.feature.currency.domain.model.Currency

@Composable
fun ProductsCarousel(
    products: List<SearchProduct>,
    currency: Currency,
    favoriteIds: Set<Long>,
    onProductClick: (Long) -> Unit,
    onFavoriteClick: (SearchProduct) -> Unit,
    onAddToCartClick: (SearchProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
    ) {
        items(products, key = { it.id }) { product ->
            ProductChatCardWrapper(
                product = product,
                currency = currency,
                isFavorite = favoriteIds.contains(product.id),
                onClick = { onProductClick(product.id) },
                onFavoriteClick = { onFavoriteClick(product) },
                onAddToCartClick = { onAddToCartClick(product) }
            )
        }
    }
}

@Composable
fun ProductChatCardWrapper(
    product: SearchProduct,
    currency: Currency,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.width(168.dp)) {
        ProductCard(
            name = product.title,
            price = product.price,
            imageUrl = product.imageUrl,
            compareAtPrice = product.compareAtPrice,
            isOnSale = product.compareAtPrice != null && product.compareAtPrice > product.price,
            isFavorite = isFavorite,
            onClick = onClick,
            onFavoriteClick = onFavoriteClick
        )
        // Cart overlay chip
        FilledIconButton(
            onClick = onAddToCartClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(6.dp)
                .size(30.dp),
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Add to Cart",
                modifier = Modifier.size(15.dp)
            )
        }
    }
}
