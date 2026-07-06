package com.shopify.carto.feature.orderdetails.data.remote.api

import com.shopify.carto.core.network.graphql.dto.GraphQlRequestDto
import com.shopify.carto.feature.orderdetails.data.remote.dto.OrderDetailsGraphQlResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderDetailsShopifyGraphQlApi {
    @POST("admin/api/{version}/graphql.json")
    suspend fun execute(
        @Path("version") version: String,
        @Body request: GraphQlRequestDto,
    ): Response<OrderDetailsGraphQlResponseDto>
}
