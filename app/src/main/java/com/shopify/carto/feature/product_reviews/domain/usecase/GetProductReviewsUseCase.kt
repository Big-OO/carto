package com.shopify.carto.feature.product_reviews.domain.usecase



import com.shopify.carto.feature.product_reviews.domain.model.ProductReviews
import com.shopify.carto.feature.product_reviews.domain.repository.ProductReviewsRepository
import javax.inject.Inject

class GetProductReviewsUseCase @Inject constructor(
    private val repository: ProductReviewsRepository
) {

    suspend operator fun invoke(productId: Long): Result<ProductReviews> {
        return repository.getProductReviews(productId)
    }
}