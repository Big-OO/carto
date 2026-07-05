package com.shopify.carto.feature.product_details.data.datasource.remote

import com.shopify.carto.feature.product_details.data.dto.ProductDetailsResponse

interface ProductDetailsRemoteDataSource {

    suspend fun getProductDetails(productId: Long): Result<ProductDetailsResponse>
}