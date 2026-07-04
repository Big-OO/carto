package com.shopify.carto.feature.search.data.remote.network

import com.shopify.carto.feature.search.data.remote.SearchShopifyApi
import com.shopify.carto.feature.search.data.remote.model.SearchProductsResponseDto
import retrofit2.Response
import javax.inject.Inject

class RetrofitSearchNetworkDataSource @Inject constructor(
    private val api: SearchShopifyApi,
) : SearchNetworkDataSource {

    override suspend fun getProductsForSearch(
        version: String,
        limit: Int,
        publishedStatus: String,
        fields: String,
    ): Response<SearchProductsResponseDto> {
        return api.getProductsForSearch(
            version = version,
            limit = limit,
            publishedStatus = publishedStatus,
            fields = fields,
        )
    }
}
