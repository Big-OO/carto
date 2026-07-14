package com.shopify.carto.feature.search.data.remote.network

import com.shopify.carto.feature.search.data.remote.model.SearchCatalogProductsResponseDto
import retrofit2.Response

interface SearchNetworkDataSource {
    suspend fun getProductsForSearch(
        version: String,
        limit: Int,
        publishedStatus: String = "published",
        fields: String = "id,title,handle,vendor,product_type,status,tags,variants,images,created_at,updated_at",
    ): Response<SearchCatalogProductsResponseDto>
}
