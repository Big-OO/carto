package com.shopify.carto.feature.product_reviews.presentation.componenets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shopify.carto.feature.product_reviews.domain.model.ProductReviews

@Composable
fun ProductReviewsListSection(
    totalReviews: Int,
    reviews: List<ProductReviews.ProductReview>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$totalReviews Reviews",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(reviews) { review ->
                ReviewItem(
                    review = review,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)
            }
        }
    }
}

@Composable
private fun ReviewItem(
    review: ProductReviews.ProductReview,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        RatingStars(rating = review.rating)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = review.body,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight.times(1.2f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = review.author,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = " • ${review.date}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}