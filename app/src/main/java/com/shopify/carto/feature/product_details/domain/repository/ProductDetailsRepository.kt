package com.shopify.carto.feature.product_details.domain.repository

import com.shopify.carto.feature.product_details.domain.model.Product

interface ProductDetailsRepository {

    suspend fun getProductDetails(productId: Long): Result<Product>
}