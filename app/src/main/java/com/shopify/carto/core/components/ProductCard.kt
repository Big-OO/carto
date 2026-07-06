package com.shopify.carto.core.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.shopify.carto.feature.currency.presentation.format.LocalCurrencyFormatter

@Composable
fun ProductCard(
    name: String,
    price: Double,
    imageUrl: String?,
    modifier: Modifier = Modifier,
    compareAtPrice: Double? = null,
    isNew: Boolean = false,
    isOnSale: Boolean = false,
    productType: String = "",
    imageCount: Int = 1,
    isGuest: Boolean = false,
    isFavorite: Boolean = false,
    onClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onGuestFavoriteClick: () -> Unit = {},
) {
    val formatter = LocalCurrencyFormatter.current
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val heartColor by animateColorAsState(
        targetValue = if (isFavorite) colors.error else colors.onSurfaceVariant,
        animationSpec = tween(durationMillis = 300),
        label = "favorite_color"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(role = Role.Button) { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = colors.surface,
            contentColor = colors.onSurface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp,
            hoveredElevation = 8.dp
        ),
        border = BorderStroke(
            width = 1.dp,
            color = colors.outlineVariant.copy(alpha = 0.3f)
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(colors.surfaceVariant.copy(alpha = 0.3f))
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Image of $name",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (isNew) {
                        BadgeChip(
                            text = "NEW",
                            containerColor = colors.primaryContainer,
                            contentColor = colors.onPrimaryContainer
                        )
                    }
                    if (isOnSale) {
                        BadgeChip(
                            text = "SALE",
                            containerColor = colors.errorContainer,
                            contentColor = colors.onErrorContainer
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(32.dp),
                    shape = CircleShape,
                    color = colors.surface.copy(alpha = 0.85f),
                    shadowElevation = 0.dp
                ) {
                    IconButton(
                        onClick = { if (isGuest) onGuestFavoriteClick() else onFavoriteClick() }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = heartColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (imageCount > 1) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = colors.scrim.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "+${imageCount - 1}",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                if (productType.isNotBlank()) {
                    Text(
                        text = productType.uppercase(),
                        style = typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = colors.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                }

                Text(
                    text = name,
                    style = typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 16.sp
                    ),
                    color = colors.onSurface,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(8.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isOnSale && compareAtPrice != null && compareAtPrice > price) {
                        Text(
                            text = formatter.format(compareAtPrice),
                            style = typography.labelSmall.copy(fontSize = 11.sp),
                            color = colors.onSurfaceVariant.copy(alpha = 0.8f),
                            textDecoration = TextDecoration.LineThrough,
                            maxLines = 1
                        )
                    }

                    Text(
                        text = formatter.format(price),
                        style = typography.titleSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        ),
                        color = if (isOnSale) colors.primary else colors.onSurface,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun BadgeChip(
    text: String,
    containerColor: Color,
    contentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        shadowElevation = 0.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            ),
            color = contentColor
        )
    }
}