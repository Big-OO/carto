package com.shopify.carto.feature.orderhistory.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.shopify.carto.core.network.graphql.dto.GraphQlErrorDto

data class OrderHistoryGraphQlResponseDto(
    @SerializedName("data") val data: OrderHistoryDataDto? = null,
    @SerializedName("errors") val errors: List<GraphQlErrorDto>? = null,
)

data class OrderHistoryDataDto(
    @SerializedName("customer") val customer: OrderHistoryCustomerDto? = null,
)

data class OrderHistoryCustomerDto(
    @SerializedName("orders") val orders: OrderHistoryConnectionDto? = null,
)

data class OrderHistoryConnectionDto(
    @SerializedName("nodes") val nodes: List<OrderHistoryOrderDto>? = null,
)

data class OrderHistoryOrderDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("displayFinancialStatus") val displayFinancialStatus: String? = null,
    @SerializedName("displayFulfillmentStatus") val displayFulfillmentStatus: String? = null,
    @SerializedName("cancelledAt") val cancelledAt: String? = null,
    @SerializedName("closedAt") val closedAt: String? = null,
    @SerializedName("paymentGatewayNames") val paymentGatewayNames: List<String>? = null,
    @SerializedName("currentSubtotalPriceSet") val currentSubtotalPriceSet: MoneySetDto? = null,
    @SerializedName("currentTotalPriceSet") val currentTotalPriceSet: MoneySetDto? = null,
    @SerializedName("currentTotalDiscountsSet") val currentTotalDiscountsSet: MoneySetDto? = null,
    @SerializedName("lineItems") val lineItems: OrderHistoryLineItemsConnectionDto? = null,
)

data class OrderHistoryLineItemsConnectionDto(
    @SerializedName("nodes") val nodes: List<OrderHistoryLineItemDto>? = null,
)

data class OrderHistoryLineItemDto(
    @SerializedName("title") val title: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("quantity") val quantity: Int? = null,
    @SerializedName("currentQuantity") val currentQuantity: Int? = null,
    @SerializedName("image") val image: ImageDto? = null,
    @SerializedName("variant") val variant: OrderHistoryVariantDto? = null,
    @SerializedName("product") val product: OrderHistoryProductDto? = null,
)

data class OrderHistoryVariantDto(
    @SerializedName("image") val image: ImageDto? = null,
    @SerializedName("product") val product: OrderHistoryProductDto? = null,
)

data class OrderHistoryProductDto(
    @SerializedName("featuredImage") val featuredImage: ImageDto? = null,
)

data class MoneySetDto(
    @SerializedName("shopMoney") val shopMoney: MoneyDto? = null,
)

data class MoneyDto(
    @SerializedName("amount") val amount: String? = null,
    @SerializedName("currencyCode") val currencyCode: String? = null,
)

data class ImageDto(
    @SerializedName("url") val url: String? = null,
    @SerializedName("altText") val altText: String? = null,
)
