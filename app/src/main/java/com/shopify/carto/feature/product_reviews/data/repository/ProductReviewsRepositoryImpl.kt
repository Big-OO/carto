package com.shopify.carto.feature.product_reviews.data.repository

import com.shopify.carto.feature.product_reviews.data.data_source.ProductReviewsRemoteDataSource
import com.shopify.carto.feature.product_reviews.data.mapper.toDomain
import com.shopify.carto.feature.product_reviews.data.mapper.toDomainException
import com.shopify.carto.feature.product_reviews.domain.model.ProductReviews
import com.shopify.carto.feature.product_reviews.domain.repository.ProductReviewsRepository
import com.google.gson.Gson
import javax.inject.Inject

class ProductReviewsRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProductReviewsRemoteDataSource,
    private val gson: Gson
) : ProductReviewsRepository {

    override suspend fun getProductReviews(productId: Long): Result<ProductReviews> {
        return remoteDataSource.getProductReviews(productId).fold(
            onSuccess = { response ->
                Result.success(response.metafields.toDomain(gson))
            },
            onFailure = { exception ->
                Result.failure(exception.toDomainException(productId))
            }
        )
    }
}