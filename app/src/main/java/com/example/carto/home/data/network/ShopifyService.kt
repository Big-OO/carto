package com.example.carto.home.data.network

import com.example.carto.home.data.network.dtos.ProductsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ShopifyApi {

    @GET("admin/api/{version}/products.json")
    suspend fun getProducts(
        @Path("version") version: String = "2026-01",
        @Query("limit") limit: Int = 50,
        @Query("fields") fields: String =
            "id,title,handle,vendor,product_type,status,variants,images,tags,created_at,updated_at"
    ): Response<ProductsResponse>

}