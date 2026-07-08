package com.shopify.carto.feature.orderdetails.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.shopify.carto.core.network.graphql.dto.GraphQlErrorDto

data class OrderDetailsGraphQlResponseDto(
    @SerializedName("data") val data: OrderDetailsDataDto? = null,
    @SerializedName("errors") val errors: List<GraphQlErrorDto>? = null,
)

data class OrderDetailsDataDto(
    @SerializedName("order") val order: OrderDetailsOrderDto? = null,
    @SerializedName("orderCancel") val orderCancel: OrderCancelPayloadDto? = null,
)

data class OrderDetailsOrderDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("confirmationNumber") val confirmationNumber: String? = null,
    @SerializedName("displayFinancialStatus") val displayFinancialStatus: String? = null,
    @SerializedName("displayFulfillmentStatus") val displayFulfillmentStatus: String? = null,
    @SerializedName("cancelledAt") val cancelledAt: String? = null,
    @SerializedName("closedAt") val closedAt: String? = null,
    @SerializedName("paymentGatewayNames") val paymentGatewayNames: List<String>? = null,
    @SerializedName("discountCodes") val discountCodes: List<String>? = null,
    @SerializedName("currentSubtotalPriceSet") val currentSubtotalPriceSet: MoneySetDto? = null,
    @SerializedName("currentTotalDiscountsSet") val currentTotalDiscountsSet: MoneySetDto? = null,
    @SerializedName("currentTotalTaxSet") val currentTotalTaxSet: MoneySetDto? = null,
    @SerializedName("currentShippingPriceSet") val currentShippingPriceSet: MoneySetDto? = null,
    @SerializedName("currentTotalPriceSet") val currentTotalPriceSet: MoneySetDto? = null,
    @SerializedName("shippingAddress") val shippingAddress: AddressDto? = null,
    @SerializedName("lineItems") val lineItems: LineItemConnectionDto? = null,
    @SerializedName("fulfillments") val fulfillments: List<FulfillmentDto>? = null,
    @SerializedName("refunds") val refunds: List<RefundDto>? = null,
)

data class LineItemConnectionDto(
    @SerializedName("nodes") val nodes: List<OrderLineItemDto>? = null,
)

data class OrderLineItemDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("sku") val sku: String? = null,
    @SerializedName("vendor") val vendor: String? = null,
    @SerializedName("variantTitle") val variantTitle: String? = null,
    @SerializedName("quantity") val quantity: Int? = null,
    @SerializedName("currentQuantity") val currentQuantity: Int? = null,
    @SerializedName("image") val image: ImageDto? = null,
    @SerializedName("variant") val variant: VariantDto? = null,
    @SerializedName("product") val product: ProductDto? = null,
    @SerializedName("originalUnitPriceSet") val originalUnitPriceSet: MoneySetDto? = null,
    @SerializedName("discountedTotalSet") val discountedTotalSet: MoneySetDto? = null,
    @SerializedName("totalDiscountSet") val totalDiscountSet: MoneySetDto? = null,
)

data class VariantDto(
    @SerializedName("image") val image: ImageDto? = null,
    @SerializedName("product") val product: ProductDto? = null,
)

data class ProductDto(
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

data class AddressDto(
    @SerializedName("name") val name: String? = null,
    @SerializedName("address1") val address1: String? = null,
    @SerializedName("city") val city: String? = null,
    @SerializedName("country") val country: String? = null,
    @SerializedName("phone") val phone: String? = null,
)

data class FulfillmentDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("trackingInfo") val trackingInfo: List<TrackingInfoDto>? = null,
)

data class TrackingInfoDto(
    @SerializedName("number") val number: String? = null,
    @SerializedName("url") val url: String? = null,
)

data class RefundDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
)

data class OrderCancelPayloadDto(
    @SerializedName("job") val job: JobDto? = null,
    @SerializedName("orderCancelUserErrors") val orderCancelUserErrors: List<UserErrorDto>? = null,
    @SerializedName("userErrors") val userErrors: List<UserErrorDto>? = null,
)

data class JobDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("done") val done: Boolean? = null,
)

data class UserErrorDto(
    @SerializedName("field") val field: List<String>? = null,
    @SerializedName("message") val message: String? = null,
)
