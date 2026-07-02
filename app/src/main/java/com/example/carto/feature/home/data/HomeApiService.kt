package com.example.carto.feature.home.data


import com.example.carto.feature.home.data.model.CollectionsResponse
import com.example.carto.feature.home.data.model.ProductDetailsResponse
import com.example.carto.feature.home.data.model.ProductsResponse
import com.example.carto.feature.home.data.model.SmartCollectionsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HomeApiService {

    @GET("admin/api/{version}/products.json")
    suspend fun getProducts(
        @Path("version") version: String = "2026-01",
        @Query("limit") limit: Int = 50,
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
        @Path("version") version: String = "2026-01"
    ): Response<CollectionsResponse>

    @GET("admin/api/{version}/collections/{collectionId}/products.json")
    suspend fun getProductsByCollection(
        @Path("version") version: String = "2026-01",
        @Path("collectionId") collectionId: Long
    ): Response<ProductsResponse>

    @GET("admin/api/{version}/smart_collections.json")
    suspend fun getBrands(
        @Path("version") version: String = "2026-01"
    ): Response<SmartCollectionsResponse>
}