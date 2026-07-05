package com.shopify.carto.feature.home.data.remote

import com.shopify.carto.feature.home.data.model.CollectionsResponse
import com.shopify.carto.feature.home.data.model.ProductDetailsResponse
import com.shopify.carto.feature.home.data.model.ProductsResponse
import com.shopify.carto.feature.home.data.model.SmartCollectionsResponse

interface HomeRemoteDataSource {

    suspend fun getProducts(): Result<ProductsResponse>

    suspend fun getProductById(
        productId: Long
    ): Result<ProductDetailsResponse>

    suspend fun getCollections(): Result<CollectionsResponse>

    suspend fun getProductsByCollection(
        collectionId: Long
    ): Result<ProductsResponse>

    suspend fun getBrands(): Result<SmartCollectionsResponse>
}