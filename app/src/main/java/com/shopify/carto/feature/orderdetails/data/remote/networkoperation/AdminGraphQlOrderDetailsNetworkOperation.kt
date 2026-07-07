package com.shopify.carto.feature.orderdetails.data.remote.networkoperation

import com.apollographql.apollo.ApolloClient
import com.shopify.carto.core.graphql.admin.CancelOrderMutation
import com.shopify.carto.core.graphql.admin.GetOrderDetailsQuery
import com.shopify.carto.core.network.graphql.dto.GraphQlErrorDto
import com.shopify.carto.core.network.qualifier.AdminApollo
import com.shopify.carto.feature.orderdetails.data.remote.dto.AddressDto
import com.shopify.carto.feature.orderdetails.data.remote.dto.FulfillmentDto
import com.shopify.carto.feature.orderdetails.data.remote.dto.ImageDto
import com.shopify.carto.feature.orderdetails.data.remote.dto.JobDto
import com.shopify.carto.feature.orderdetails.data.remote.dto.LineItemConnectionDto
import com.shopify.carto.feature.orderdetails.data.remote.dto.MoneyDto
import com.shopify.carto.feature.orderdetails.data.remote.dto.MoneySetDto
import com.shopify.carto.feature.orderdetails.data.remote.dto.OrderCancelPayloadDto
import com.shopify.carto.feature.orderdetails.data.remote.dto.OrderDetailsDataDto
import com.shopify.carto.feature.orderdetails.data.remote.dto.OrderDetailsGraphQlResponseDto
import com.shopify.carto.feature.orderdetails.data.remote.dto.OrderDetailsOrderDto
import com.shopify.carto.feature.orderdetails.data.remote.dto.OrderLineItemDto
import com.shopify.carto.feature.orderdetails.data.remote.dto.ProductDto
import com.shopify.carto.feature.orderdetails.data.remote.dto.RefundDto
import com.shopify.carto.feature.orderdetails.data.remote.dto.TrackingInfoDto
import com.shopify.carto.feature.orderdetails.data.remote.dto.UserErrorDto
import com.shopify.carto.feature.orderdetails.data.remote.dto.VariantDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AdminGraphQlOrderDetailsNetworkOperation @Inject constructor(
    @param:AdminApollo private val apolloClient: ApolloClient,
) : OrderDetailsNetworkOperation {

    override suspend fun getOrderDetails(orderId: String): OrderDetailsGraphQlResponseDto {
        val response = apolloClient.query(
            GetOrderDetailsQuery(orderId = orderId),
        ).execute()

        val order = response.data?.order

        return withContext(Dispatchers.Default) {
            OrderDetailsGraphQlResponseDto(
                data = OrderDetailsDataDto(
                    order = order?.let {
                        OrderDetailsOrderDto(
                            id = it.id,
                            name = it.name,
                            createdAt = it.createdAt.asText(),
                            confirmationNumber = it.confirmationNumber,
                            displayFinancialStatus = it.displayFinancialStatus.asText(),
                            displayFulfillmentStatus = it.displayFulfillmentStatus.asText(),
                            cancelledAt = it.cancelledAt.asTextOrNull(),
                            closedAt = it.closedAt.asTextOrNull(),
                            paymentGatewayNames = it.paymentGatewayNames,
                            discountCodes = it.discountCodes,
                            currentSubtotalPriceSet = MoneySetDto(
                                shopMoney = MoneyDto(
                                    amount = it.currentSubtotalPriceSet.shopMoney.amount.asText(),
                                    currencyCode = it.currentSubtotalPriceSet.shopMoney.currencyCode.asText(),
                                ),
                            ),
                            currentTotalDiscountsSet = MoneySetDto(
                                shopMoney = MoneyDto(
                                    amount = it.currentTotalDiscountsSet.shopMoney.amount.asText(),
                                    currencyCode = it.currentTotalDiscountsSet.shopMoney.currencyCode.asText(),
                                ),
                            ),
                            currentTotalTaxSet = MoneySetDto(
                                shopMoney = MoneyDto(
                                    amount = it.currentTotalTaxSet.shopMoney.amount.asText(),
                                    currencyCode = it.currentTotalTaxSet.shopMoney.currencyCode.asText(),
                                ),
                            ),
                            currentShippingPriceSet = MoneySetDto(
                                shopMoney = MoneyDto(
                                    amount = it.currentShippingPriceSet.shopMoney.amount.asText(),
                                    currencyCode = it.currentShippingPriceSet.shopMoney.currencyCode.asText(),
                                ),
                            ),
                            currentTotalPriceSet = MoneySetDto(
                                shopMoney = MoneyDto(
                                    amount = it.currentTotalPriceSet.shopMoney.amount.asText(),
                                    currencyCode = it.currentTotalPriceSet.shopMoney.currencyCode.asText(),
                                ),
                            ),
                            shippingAddress = it.shippingAddress?.let { address ->
                                AddressDto(
                                    name = address.name,
                                    address1 = address.address1,
                                    city = address.city,
                                    country = address.country,
                                    phone = address.phone,
                                )
                            },
                            lineItems = LineItemConnectionDto(
                                nodes = it.lineItems.nodes.map { lineItem ->
                                    OrderLineItemDto(
                                        id = lineItem.id,
                                        title = lineItem.title,
                                        name = lineItem.name,
                                        sku = lineItem.sku,
                                        vendor = lineItem.vendor,
                                        variantTitle = lineItem.variantTitle,
                                        quantity = lineItem.quantity,
                                        currentQuantity = lineItem.currentQuantity,
                                        image = lineItem.image?.let { image ->
                                            ImageDto(
                                                url = image.url.asTextOrNull(),
                                                altText = image.altText,
                                            )
                                        },
                                        variant = lineItem.variant?.let { variant ->
                                            VariantDto(
                                                image = variant.image?.let { image ->
                                                    ImageDto(
                                                        url = image.url.asTextOrNull(),
                                                        altText = image.altText,
                                                    )
                                                },
                                                product = ProductDto(
                                                    featuredImage = variant.product.featuredImage?.let { image ->
                                                        ImageDto(
                                                            url = image.url.asTextOrNull(),
                                                            altText = image.altText,
                                                        )
                                                    },
                                                ),
                                            )
                                        },
                                        product = lineItem.product?.let { product ->
                                            ProductDto(
                                                featuredImage = product.featuredImage?.let { image ->
                                                    ImageDto(
                                                        url = image.url.asTextOrNull(),
                                                        altText = image.altText,
                                                    )
                                                },
                                            )
                                        },
                                        originalUnitPriceSet = MoneySetDto(
                                            shopMoney = MoneyDto(
                                                amount = lineItem.originalUnitPriceSet.shopMoney.amount.asText(),
                                                currencyCode = lineItem.originalUnitPriceSet.shopMoney.currencyCode.asText(),
                                            ),
                                        ),
                                        discountedTotalSet = MoneySetDto(
                                            shopMoney = MoneyDto(
                                                amount = lineItem.discountedTotalSet.shopMoney.amount.asText(),
                                                currencyCode = lineItem.discountedTotalSet.shopMoney.currencyCode.asText(),
                                            ),
                                        ),
                                        totalDiscountSet = MoneySetDto(
                                            shopMoney = MoneyDto(
                                                amount = lineItem.totalDiscountSet.shopMoney.amount.asText(),
                                                currencyCode = lineItem.totalDiscountSet.shopMoney.currencyCode.asText(),
                                            ),
                                        ),
                                    )
                                },
                            ),
                            fulfillments = it.fulfillments.map { fulfillment ->
                                FulfillmentDto(
                                    id = fulfillment.id,
                                    status = fulfillment.status.asText(),
                                    createdAt = fulfillment.createdAt.asTextOrNull(),
                                    trackingInfo = fulfillment.trackingInfo.map { trackingInfo ->
                                        TrackingInfoDto(
                                            number = trackingInfo.number,
                                            url = trackingInfo.url.asTextOrNull(),
                                        )
                                    },
                                )
                            },
                            refunds = it.refunds.map { refund ->
                                RefundDto(
                                    id = refund.id,
                                    createdAt = refund.createdAt.asTextOrNull(),
                                )
                            },
                        )
                    },
                ),
                errors = response.errors?.map { error ->
                    GraphQlErrorDto(message = error.message)
                },
            )
        }
    }

    override suspend fun cancelOrder(orderId: String): OrderDetailsGraphQlResponseDto {
        val response = apolloClient.mutation(
            CancelOrderMutation(orderId = orderId),
        ).execute()

        val payload = response.data?.orderCancel

        return OrderDetailsGraphQlResponseDto(
            data = OrderDetailsDataDto(
                orderCancel = payload?.let {
                    OrderCancelPayloadDto(
                        job = it.job?.let { job ->
                            JobDto(
                                id = job.id,
                                done = job.done,
                            )
                        },
                        orderCancelUserErrors = it.orderCancelUserErrors.map { error ->
                            UserErrorDto(
                                field = error.field?.map { field -> field.asText() },
                                message = error.message,
                            )
                        },
                        userErrors = it.userErrors.map { error ->
                            UserErrorDto(
                                field = error.field?.map { field -> field.asText() },
                                message = error.message,
                            )
                        },
                    )
                },
            ),
            errors = response.errors?.map { error ->
                GraphQlErrorDto(message = error.message)
            },
        )
    }

    private fun Any?.asText(): String {
        return this?.toString().orEmpty()
    }

    private fun Any?.asTextOrNull(): String? {
        return this?.toString()?.takeIf { it.isNotBlank() }
    }
}
