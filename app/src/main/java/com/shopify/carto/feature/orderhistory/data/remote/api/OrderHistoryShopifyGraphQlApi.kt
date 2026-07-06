package com.shopify.carto.feature.orderhistory.data.remote.api

import com.shopify.carto.core.network.graphql.dto.GraphQlRequestDto
import com.shopify.carto.feature.orderhistory.data.remote.dto.OrderHistoryGraphQlResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderHistoryShopifyGraphQlApi {
    @POST("admin/api/{version}/graphql.json")
    suspend fun getCustomerOrders(
        @Path("version") version: String,
        @Body request: GraphQlRequestDto,
    ): Response<OrderHistoryGraphQlResponseDto>
}
