package com.shopify.carto.feature.product_details.data.repository

import com.shopify.carto.feature.product_details.data.datasource.remote.ProductDetailsRemoteDataSource
import com.shopify.carto.feature.product_details.data.mapper.toDomain
import com.shopify.carto.feature.product_details.domain.model.Product
import com.shopify.carto.feature.product_details.domain.repository.ProductDetailsRepository
import javax.inject.Inject

class ProductDetailsRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProductDetailsRemoteDataSource
) : ProductDetailsRepository {

    override suspend fun getProductDetails(productId: Long): Result<Product> {
        return remoteDataSource.getProductDetails(productId).map { response ->
            response.product.toDomain()
        }
    }
}