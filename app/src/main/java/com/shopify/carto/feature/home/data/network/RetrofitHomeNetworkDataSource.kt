package com.shopify.carto.feature.home.data.network

import com.shopify.carto.core.network.config.ShopifyConfig
import com.shopify.carto.feature.home.data.HomeApiService
import com.shopify.carto.feature.home.data.model.CollectionsResponse
import com.shopify.carto.feature.home.data.model.ProductDetailsResponse
import com.shopify.carto.feature.home.data.model.PriceRulesResponse
import com.shopify.carto.feature.home.data.model.ProductsResponse
import com.shopify.carto.feature.home.data.model.SmartCollectionsResponse
import retrofit2.Response
import javax.inject.Inject

class RetrofitHomeNetworkDataSource @Inject constructor(
    private val api: HomeApiService,
    private val config: ShopifyConfig,
) : HomeNetworkDataSource {

    override suspend fun getProducts(): Response<ProductsResponse> {
        return api.getProducts(version = config.apiVersion)
    }

    override suspend fun getProductById(productId: Long): Response<ProductDetailsResponse> {
        return api.getProductById(version = config.apiVersion, productId = productId)
    }

    override suspend fun getCollections(): Response<CollectionsResponse> {
        return api.getCollections(version = config.apiVersion)
    }

    override suspend fun getProductsByCollection(collectionId: Long): Response<ProductsResponse> {
        return api.getProductsByCollection(version = config.apiVersion, collectionId = collectionId)
    }

    override suspend fun getBrands(): Response<SmartCollectionsResponse> {
        return api.getBrands(version = config.apiVersion)
    }

    override suspend fun getPriceRules(limit: Int): Response<PriceRulesResponse> {
        return api.getPriceRules(
            version = config.apiVersion,
            limit = limit,
        )
    }
}
