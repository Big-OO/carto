package com.example.carto.feature.home.presentation.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.carto.feature.home.domain.model.Product

@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    isGuest: Boolean = false,
    onClick: (Product) -> Unit = {},
    onFavoriteClick: (Product) -> Unit = {},
    onGuestFavoriteClick: () -> Unit = {},
) {
    var isFavorite by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier.clickable { onClick(product) },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorScheme.surfaceVariant)
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
                            .padding(6.dp),
                        color = if (product.isNew) colorScheme.tertiary else colorScheme.error,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (product.isNew) "NEW" else "SALE",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                IconButton(
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = colorScheme.surface,
                        contentColor = colorScheme.primary,
                    ),
                    onClick = {
                        if (isGuest) {
                            onGuestFavoriteClick()
                        } else {
                            isFavorite = !isFavorite
                            onFavoriteClick(product)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) colorScheme.error else colorScheme.primary,
                    )
                }
                if (product.imageCount > 1) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(6.dp),
                        color = colorScheme.primary.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "+${product.imageCount - 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = product.productType,
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.primary
            )

            Text(product.name, style = MaterialTheme.typography.bodyMedium, maxLines = 1)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "$${"%.2f".format(product.price)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
                if (product.isOnSale && product.compareAtPrice != null) {
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "$${"%.2f".format(product.compareAtPrice)}",
                        style = MaterialTheme.typography.labelSmall.copy(textDecoration = TextDecoration.LineThrough),
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            if (product.isLowStock) {
                Text(
                    text = "Only ${product.totalStock} left",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.error,
                )
            }

            if (product.variantCount > 1) {
                Text(
                    text = "${product.variantCount} options",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
