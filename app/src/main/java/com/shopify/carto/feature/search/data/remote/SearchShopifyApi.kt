package com.shopify.carto.feature.search.data.remote

import com.shopify.carto.feature.search.data.remote.model.SearchProductsResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SearchShopifyApi {
    @GET("admin/api/{version}/products.json")
    suspend fun getProductsForSearch(
        @Path("version") version: String,
        @Query("limit") limit: Int = 250,
        @Query("published_status") publishedStatus: String = "published",
        @Query("fields") fields: String = "id,title,handle,vendor,product_type,status,tags,variants,images,created_at,updated_at",
    ): Response<SearchProductsResponseDto>
}
