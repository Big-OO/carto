package com.shopify.carto.feature.home.data.network

import com.shopify.carto.feature.home.data.model.CollectionsResponse
import com.shopify.carto.feature.home.data.model.ProductDetailsResponse
import com.shopify.carto.feature.home.data.model.ProductsResponse
import com.shopify.carto.feature.home.data.model.SmartCollectionsResponse
import retrofit2.Response

interface HomeNetworkDataSource {
    suspend fun getProducts(): Response<ProductsResponse>

    suspend fun getProductById(productId: Long): Response<ProductDetailsResponse>

    suspend fun getCollections(): Response<CollectionsResponse>

    suspend fun getProductsByCollection(collectionId: Long): Response<ProductsResponse>

    suspend fun getBrands(): Response<SmartCollectionsResponse>
}
