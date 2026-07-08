package com.shopify.carto.feature.orderhistory.data.remote.networkoperation

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.shopify.carto.core.graphql.admin.GetCustomerOrdersQuery
import com.shopify.carto.core.network.graphql.dto.GraphQlErrorDto
import com.shopify.carto.core.network.qualifier.AdminApollo
import com.shopify.carto.feature.orderhistory.data.remote.dto.ImageDto
import com.shopify.carto.feature.orderhistory.data.remote.dto.MoneyDto
import com.shopify.carto.feature.orderhistory.data.remote.dto.MoneySetDto
import com.shopify.carto.feature.orderhistory.data.remote.dto.OrderHistoryConnectionDto
import com.shopify.carto.feature.orderhistory.data.remote.dto.OrderHistoryCustomerDto
import com.shopify.carto.feature.orderhistory.data.remote.dto.OrderHistoryDataDto
import com.shopify.carto.feature.orderhistory.data.remote.dto.OrderHistoryGraphQlResponseDto
import com.shopify.carto.feature.orderhistory.data.remote.dto.OrderHistoryLineItemDto
import com.shopify.carto.feature.orderhistory.data.remote.dto.OrderHistoryLineItemsConnectionDto
import com.shopify.carto.feature.orderhistory.data.remote.dto.OrderHistoryOrderDto
import com.shopify.carto.feature.orderhistory.data.remote.dto.OrderHistoryProductDto
import com.shopify.carto.feature.orderhistory.data.remote.dto.OrderHistoryVariantDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AdminGraphQlOrderHistoryNetworkOperation @Inject constructor(
    @param:AdminApollo private val apolloClient: ApolloClient,
) : OrderHistoryNetworkOperation {

    override suspend fun getCustomerOrders(
        customerGid: String,
        first: Int,
    ): OrderHistoryGraphQlResponseDto {
        val response = apolloClient.query(
            GetCustomerOrdersQuery(
                customerId = customerGid,
                first = first,
            ),
        ).execute()

        return withContext(Dispatchers.Default) {
            OrderHistoryGraphQlResponseDto(
                data = OrderHistoryDataDto(
                    customer = response.data?.customer?.let {
                        OrderHistoryCustomerDto(
                            orders = OrderHistoryConnectionDto(nodes = toOderHistoryDtoList(response)),
                        )
                    },
                ),
                errors = response.errors?.map { error ->
                    GraphQlErrorDto(message = error.message)
                },
            )
        }
    }

    private fun toOderHistoryDtoList(response: ApolloResponse<GetCustomerOrdersQuery.Data>): List<OrderHistoryOrderDto> {
        return response.data
            ?.customer
            ?.orders
            ?.nodes
            .orEmpty()
            .map { order ->
                OrderHistoryOrderDto(
                    id = order.id,
                    name = order.name,
                    createdAt = order.createdAt.asText(),
                    displayFinancialStatus = order.displayFinancialStatus.asText(),
                    displayFulfillmentStatus = order.displayFulfillmentStatus.asText(),
                    cancelledAt = order.cancelledAt.asTextOrNull(),
                    closedAt = order.closedAt.asTextOrNull(),
                    paymentGatewayNames = order.paymentGatewayNames,
                    currentSubtotalPriceSet = MoneySetDto(
                        shopMoney = MoneyDto(
                            amount = order.currentSubtotalPriceSet.shopMoney.amount.asText(),
                            currencyCode = order.currentSubtotalPriceSet.shopMoney.currencyCode.asText(),
                        ),
                    ),
                    currentTotalPriceSet = MoneySetDto(
                        shopMoney = MoneyDto(
                            amount = order.currentTotalPriceSet.shopMoney.amount.asText(),
                            currencyCode = order.currentTotalPriceSet.shopMoney.currencyCode.asText(),
                        ),
                    ),
                    currentTotalDiscountsSet = MoneySetDto(
                        shopMoney = MoneyDto(
                            amount = order.currentTotalDiscountsSet.shopMoney.amount.asText(),
                            currencyCode = order.currentTotalDiscountsSet.shopMoney.currencyCode.asText(),
                        ),
                    ),
                    lineItems = OrderHistoryLineItemsConnectionDto(
                        nodes = order.lineItems.nodes.map { lineItem ->
                            OrderHistoryLineItemDto(
                                title = lineItem.title,
                                name = lineItem.name,
                                quantity = lineItem.quantity,
                                currentQuantity = lineItem.currentQuantity,
                                image = lineItem.image?.let { image ->
                                    ImageDto(
                                        url = image.url.asTextOrNull(),
                                        altText = image.altText,
                                    )
                                },
                                variant = lineItem.variant?.let { variant ->
                                    OrderHistoryVariantDto(
                                        image = variant.image?.let { image ->
                                            ImageDto(
                                                url = image.url.asTextOrNull(),
                                                altText = image.altText,
                                            )
                                        },
                                        product = OrderHistoryProductDto(
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
                                    OrderHistoryProductDto(
                                        featuredImage = product.featuredImage?.let { image ->
                                            ImageDto(
                                                url = image.url.asTextOrNull(),
                                                altText = image.altText,
                                            )
                                        },
                                    )
                                },
                            )
                        },
                    ),
                )
            }
    }

    private fun Any?.asText(): String {
        return this?.toString().orEmpty()
    }

    private fun Any?.asTextOrNull(): String? {
        return this?.toString()?.takeIf { it.isNotBlank() }
    }
}
