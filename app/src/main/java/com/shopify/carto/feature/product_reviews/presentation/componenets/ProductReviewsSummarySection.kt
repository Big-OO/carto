package com.shopify.carto.feature.product_reviews.presentation.componenets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shopify.carto.feature.product_reviews.domain.model.ProductReviews

@Composable
fun ProductReviewsSummarySection(
    summary: ProductReviews.ProductReviewsSummary,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = summary.averageRating.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp,
                color = Color.Black
            )

            RatingStars(
                rating = summary.averageRating.toInt(),
                starSize = 16.dp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = "${summary.totalReviews} Ratings",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            (5 downTo 1).forEach { stars ->
                RatingDistributionRow(
                    stars = stars,
                    progress = summary.ratingDistribution[stars] ?: 0f
                )
            }
        }
    }
}

@Composable
private fun RatingDistributionRow(stars: Int, progress: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        RatingStars(
            rating = stars,
            maxStars = 5,
            starSize = 12.dp
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .width(100.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = progress)
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black)
            )
        }
    }
}

@Composable
fun RatingStars(
    rating: Int,
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    starSize: Dp = 16.dp
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        repeat(maxStars) { index ->
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = if (index < rating) Color(0xFFFFC107) else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(starSize)
            )
        }
    }
}