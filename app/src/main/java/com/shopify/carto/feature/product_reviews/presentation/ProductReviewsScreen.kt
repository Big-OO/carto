package com.example.carto.product_reviews.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shopify.carto.feature.product_reviews.domain.model.ProductReviews
import com.shopify.carto.feature.product_reviews.presentation.ProductReviewsViewModel
import com.shopify.carto.feature.product_reviews.presentation.componenets.ProductReviewsHeaderSection
import com.shopify.carto.feature.product_reviews.presentation.componenets.ProductReviewsListSection
import com.shopify.carto.feature.product_reviews.presentation.componenets.ProductReviewsSummarySection
import com.shopify.carto.feature.product_reviews.presentation.ProductReviewsUiState

@Composable
fun ProductReviewsScreen(
    productId: String,
    onBackClick: () -> Unit,
    viewModel: ProductReviewsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProductReviews(productId)
    }

    ProductReviewsScreenContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onRetryClick = { viewModel.loadProductReviews(productId) }
    )
}

@Composable
private fun ProductReviewsScreenContent(
    uiState: ProductReviewsUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit
) {
    Scaffold(
        topBar = {
            ProductReviewsHeaderSection(
                onBackClick = onBackClick,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> LoadingState()
                uiState.errorMessage != null -> ErrorState(
                    message = uiState.errorMessage,
                    onRetryClick = onRetryClick
                )
                uiState.reviews != null -> ReviewsContent(
                    data = uiState.reviews
                )
            }
        }
    }
}

@Composable
private fun ReviewsContent(data: ProductReviews) {
    Column(modifier = Modifier.fillMaxSize()) {
        ProductReviewsSummarySection(
            summary = data.summary,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        ProductReviewsListSection(
            totalReviews = data.summary.totalReviews,
            reviews = data.reviews,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp) // Added horizontal padding here to match the summary section
        )
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            TextButton(onClick = onRetryClick) {
                Text(text = "Try Again")
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ProductReviewsScreenPreview() {
    val dummySummary = ProductReviews.ProductReviewsSummary(
        averageRating = 4.0,
        totalReviews = 1034,
        ratingDistribution = mapOf(
            5 to 0.6f,
            4 to 0.2f,
            3 to 0.1f,
            2 to 0.05f,
            1 to 0.05f
        )
    )

    val dummyReviewList = listOf(
        ProductReviews.ProductReview(
            id = 1L,
            rating = 5,
            title = "Great quality!",
            body = "The item is very good, my son likes it very much and plays every day.",
            author = "Wade Warren",
            date = "6 days ago"
        ),
        ProductReviews.ProductReview(
            id = 2L,
            rating = 4,
            title = "Fast delivery",
            body = "The seller is very fast in sending packet, I just bought it and the item arrived in just 1 day!",
            author = "Guy Hawkins",
            date = "1 week ago"
        ),
        ProductReviews.ProductReview(
            id = 3L,
            rating = 4,
            title = "Highly recommend",
            body = "I just bought it and the stuff is really good! I highly recommend it!",
            author = "Robert Fox",
            date = "2 weeks ago"
        )
    )

    val dummyReviewsData = ProductReviews(
        summary = dummySummary,
        reviews = dummyReviewList
    )

    ProductReviewsScreenContent(
        uiState = ProductReviewsUiState(
            isLoading = false,
            reviews = dummyReviewsData,
            errorMessage = null
        ),
        onBackClick = {},
        onRetryClick = {}
    )
}