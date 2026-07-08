package com.shopify.carto.feature.home.presentation.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.shopify.carto.feature.home.domain.model.Product
import com.shopify.carto.feature.currency.domain.model.Currency
import com.shopify.carto.ui.theme.CartoTheme


@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    isGuest: Boolean = false,
    isFavorite: Boolean = false,
    currency: Currency = Currency.USD,
    onClick: (Product) -> Unit = {},
    onFavoriteClick: (Product) -> Unit = {},
    onGuestFavoriteClick: () -> Unit = {},
) {

    val colors = MaterialTheme.colorScheme

    Card(
        modifier = modifier.clickable { onClick(product) },
        shape = RoundedCornerShape(14.dp),
    ) {

        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {

                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (product.isNew || product.isOnSale) {

                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = if (product.isNew)
                            Color(0xFF2E7D32)
                        else
                            colors.error
                    ) {

                        Text(
                            text = if (product.isNew) "NEW" else "SALE",
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            ),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )

                    }

                }

                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(42.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {

                    IconButton(
                        onClick = {

                            if (isGuest) {
                                onGuestFavoriteClick()
                            } else {
                                onFavoriteClick(product)
                            }

                        }
                    ) {

                        Icon(
                            imageVector =
                                if (isFavorite)
                                    Icons.Filled.Favorite
                                else
                                    Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint =
                                if (isFavorite)
                                    Color.Red
                                else
                                    Color.Black
                        )

                    }

                }

                if (product.imageCount > 1) {

                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = .65f)
                    ) {

                        Text(
                            "+${product.imageCount - 1}",
                            modifier = Modifier.padding(
                                horizontal = 6.dp,
                                vertical = 2.dp
                            ),
                            color = CartoTheme.colors.primary,
                            style = MaterialTheme.typography.labelSmall
                        )

                    }

                }

            }

            Column(
                modifier = Modifier.background(Color.White).padding(top=8.dp)
            ) {
                Text(
                    text = product.productType.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = CartoTheme.colors.primary
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = CartoTheme.colors.primary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    minLines = 2
                )

                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        if (product.price > 0.0) {
                            Text(
                                text = formatPrice(product.price, currency),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = CartoTheme.colors.primary
                            )
                        }

                        if (product.isOnSale &&
                            product.compareAtPrice != null
                        ) {

                            Spacer(Modifier.width(6.dp))

                            Text(
                                text = formatPrice(product.compareAtPrice, currency),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    textDecoration = TextDecoration.LineThrough
                                ),
                                color = colors.onSurfaceVariant
                            )

                        }

                    }

                    if (product.variantCount > 1) {

                        Text(
                            "${product.variantCount} options",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.onSurfaceVariant
                        )

                    } else {

                        Spacer(Modifier.width(1.dp))

                    }

                }

            }

        }

    }

}

private fun formatPrice(priceInEgp: Double, currency: Currency): String {
    return when (currency) {
        Currency.USD -> {
            val usdPrice = priceInEgp / 50.0
            "$${"%,.2f".format(usdPrice)}"
        }
        Currency.EUR -> {
            val eurPrice = priceInEgp / 55.0
            "€${"%,.2f".format(eurPrice)}"
        }
        Currency.EGP -> {
            "EGP ${"%,.0f".format(priceInEgp)}"
        }
    }
}
