//package com.shopify.carto.feature.home.presentation.screens.sections
//
package com.shopify.carto.feature.home.presentation.screens.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shopify.carto.R
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
                ProductCard(
                    product = product,
                    modifier = Modifier.width(190.dp),
                    isGuest = isGuest,
                    isFavorite = favoriteIds.contains(product.id),
                    onClick = { onProductClick(product) },
                    onFavoriteClick = onFavoriteClick,
                    onGuestFavoriteClick = onGuestFavoriteClick
                )
            }
        }
    }
}

//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.shopify.carto.feature.home.domain.model.Product
//import com.shopify.carto.feature.home.presentation.screens.components.ProductCard
//import com.shopify.carto.feature.home.presentation.screens.components.SectionHeader
//
//@Composable
//fun ProductsSection(
//    products: List<Product>,
//    isGuest: Boolean,
//    favoriteIds: Set<Long>,
//    onSeeAll: () -> Unit,
//    onProductClick: (Product) -> Unit,
//    onFavoriteClick: (Product) -> Unit,
//    onGuestFavoriteClick: () -> Unit,
//) {
//    Column {
//        SectionHeader(
//            title = "All Products",
//            onSeeAll = onSeeAll,
//        )
//
//        Spacer(Modifier.height(8.dp))
//        LazyRow(
//            horizontalArrangement = Arrangement.spacedBy(12.dp),
//        ) {
//            items(products, key = { it.id }) { product ->
//                ProductCard(
//                    product = product,
//                    modifier = Modifier.width(190.dp),
//                    isGuest = isGuest,
//                    isFavorite = favoriteIds.contains(product.id),
//                    onClick = { onProductClick(product) },
//                    onFavoriteClick = onFavoriteClick,
//                    onGuestFavoriteClick = onGuestFavoriteClick
//                )
//            }
//        }
//    }
//}
