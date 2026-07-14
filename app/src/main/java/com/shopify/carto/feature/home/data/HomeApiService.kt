package com.shopify.carto.feature.home.data

import com.shopify.carto.feature.home.data.model.CollectionsResponse
import com.shopify.carto.feature.home.data.model.PriceRulesResponse
import com.shopify.carto.feature.home.data.model.ProductDetailsResponse
import com.shopify.carto.feature.home.data.model.ProductsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HomeApiService {

    @GET("admin/api/{version}/products.json")
    suspend fun getProducts(
        @Path("version") version: String = "2026-01",
        @Query("limit") limit: Int = 250,
        @Query("fields") fields: String =
            "id,title,handle,vendor,product_type,status,variants,images,tags,created_at,updated_at"
    ): Response<ProductsResponse>

    @GET("admin/api/{version}/products/{productId}.json")
    suspend fun getProductById(
        @Path("version") version: String = "2026-01",
        @Path("productId") productId: Long,
        @Query("fields") fields: String =
            "id,title,handle,vendor,product_type,status,variants,images,tags,created_at,updated_at"
    ): Response<ProductDetailsResponse>

    @GET("admin/api/{version}/custom_collections.json")
    suspend fun getCollections(
        @Path("version") version: String = "2026-01",
        @Query("limit") limit: Int = 250,
        @Query("fields") fields: String =
            "id,title,handle,body_html,image,template_suffix,published_at,published_scope,admin_graphql_api_id,updated_at"
    ): Response<CollectionsResponse>

    @GET("admin/api/{version}/collections/{collectionId}/products.json")
    suspend fun getProductsByCollection(
        @Path("version") version: String = "2026-01",
        @Path("collectionId") collectionId: Long,
        @Query("limit") limit: Int = 250,
        @Query("fields") fields: String =
            "id,title,handle,vendor,product_type,status,variants,images,tags,created_at,updated_at"
    ): Response<ProductsResponse>

    @GET("admin/api/{version}/custom_collections.json")
    suspend fun getBrands(
        @Path("version") version: String = "2026-01",
        @Query("limit") limit: Int = 250,
        @Query("fields") fields: String =
            "id,title,handle,body_html,image,template_suffix,published_at,published_scope,admin_graphql_api_id,updated_at"
    ): Response<CollectionsResponse>

    @GET("admin/api/{version}/price_rules.json")
    suspend fun getPriceRules(
        @Path("version") version: String = "2026-01",
        @Query("limit") limit: Int = 50,
    ): Response<PriceRulesResponse>
}