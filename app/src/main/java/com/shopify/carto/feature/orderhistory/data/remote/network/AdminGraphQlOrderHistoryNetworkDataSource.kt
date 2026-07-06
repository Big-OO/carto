package com.shopify.carto.feature.orderhistory.data.remote.network

import com.shopify.carto.core.network.graphql.dto.GraphQlRequestDto
import com.shopify.carto.feature.orderhistory.data.remote.api.OrderHistoryShopifyGraphQlApi
import com.shopify.carto.feature.orderhistory.data.remote.dto.OrderHistoryGraphQlResponseDto
import retrofit2.Response
import javax.inject.Inject

class AdminGraphQlOrderHistoryNetworkDataSource @Inject constructor(
    private val api: OrderHistoryShopifyGraphQlApi,
) : OrderHistoryNetworkDataSource {
    override suspend fun getCustomerOrders(
        version: String,
        customerGid: String,
        first: Int,
    ): Response<OrderHistoryGraphQlResponseDto> {
        return api.getCustomerOrders(
            version = version,
            request = GraphQlRequestDto(
                query = GET_CUSTOMER_ORDERS_QUERY,
                variables = mapOf(
                    "customerId" to customerGid,
                    "first" to first,
                ),
            ),
        )
    }

    private companion object {
        val GET_CUSTOMER_ORDERS_QUERY = """
            query GetCustomerOrders(${'$'}customerId: ID!, ${'$'}first: Int!) {
              customer(id: ${'$'}customerId) {
                orders(first: ${'$'}first, reverse: true) {
                  nodes {
                    id
                    name
                    createdAt
                    displayFinancialStatus
                    displayFulfillmentStatus
                    cancelledAt
                    closedAt
                    paymentGatewayNames
                    currentSubtotalPriceSet {
                      shopMoney { amount currencyCode }
                    }
                    currentTotalPriceSet {
                      shopMoney { amount currencyCode }
                    }
                    currentTotalDiscountsSet {
                      shopMoney { amount currencyCode }
                    }
                    lineItems(first: 20) {
                      nodes {
                        title
                        name
                        quantity
                        currentQuantity
                        image { url altText }
                        variant {
                          image { url altText }
                          product { featuredImage { url altText } }
                        }
                        product { featuredImage { url altText } }
                      }
                    }
                  }
                }
              }
            }
        """.trimIndent()
    }
}
