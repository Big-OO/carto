package com.shopify.carto.feature.home.domain.repository

import com.shopify.carto.feature.home.domain.model.Brand
import com.shopify.carto.feature.home.domain.model.Product
import com.shopify.carto.feature.home.domain.model.Category

interface HomeRepository {
    suspend fun getProducts(): Result<List<Product>>
    suspend fun getCategories(): Result<List<Category>>
    suspend fun getProductById(productId: Long): Result<Product>

    suspend fun getProductsByCategory(
        collectionId: Long
    ): Result<List<Product>>

    suspend fun getBrands(): Result<List<Brand>>
}

