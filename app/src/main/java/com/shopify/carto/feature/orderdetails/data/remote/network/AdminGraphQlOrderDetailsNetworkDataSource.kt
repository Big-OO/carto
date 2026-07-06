package com.shopify.carto.feature.orderdetails.data.remote.network

import com.shopify.carto.core.network.graphql.dto.GraphQlRequestDto
import com.shopify.carto.feature.orderdetails.data.remote.api.OrderDetailsShopifyGraphQlApi
import com.shopify.carto.feature.orderdetails.data.remote.dto.OrderDetailsGraphQlResponseDto
import retrofit2.Response
import javax.inject.Inject

class AdminGraphQlOrderDetailsNetworkDataSource @Inject constructor(
    private val api: OrderDetailsShopifyGraphQlApi,
) : OrderDetailsNetworkDataSource {

    override suspend fun getOrderDetails(
        version: String,
        orderId: String,
    ): Response<OrderDetailsGraphQlResponseDto> {
        return api.execute(
            version = version,
            request = GraphQlRequestDto(
                query = GET_ORDER_DETAILS_QUERY,
                variables = mapOf("orderId" to orderId),
            ),
        )
    }

    override suspend fun cancelOrder(
        version: String,
        orderId: String,
    ): Response<OrderDetailsGraphQlResponseDto> {
        return api.execute(
            version = version,
            request = GraphQlRequestDto(
                query = CANCEL_ORDER_MUTATION,
                variables = mapOf("orderId" to orderId),
            ),
        )
    }

    private companion object {
         val GET_ORDER_DETAILS_QUERY = """
            query GetOrderDetails(${'$'}orderId: ID!) {
              order(id: ${'$'}orderId) {
                id
                name
                createdAt
                confirmationNumber
                displayFinancialStatus
                displayFulfillmentStatus
                cancelledAt
                closedAt
                paymentGatewayNames
                discountCodes
                currentSubtotalPriceSet { shopMoney { amount currencyCode } }
                currentTotalDiscountsSet { shopMoney { amount currencyCode } }
                currentTotalTaxSet { shopMoney { amount currencyCode } }
                currentShippingPriceSet { shopMoney { amount currencyCode } }
                currentTotalPriceSet { shopMoney { amount currencyCode } }
                shippingAddress {
                  name
                  address1
                  city
                  country
                  phone
                }
                lineItems(first: 50) {
                  nodes {
                    id
                    title
                    name
                    sku
                    vendor
                    variantTitle
                    quantity
                    currentQuantity
                    image { url altText }
                    variant {
                      image { url altText }
                      product { featuredImage { url altText } }
                    }
                    product { featuredImage { url altText } }
                    originalUnitPriceSet { shopMoney { amount currencyCode } }
                    discountedTotalSet(withCodeDiscounts: true) { shopMoney { amount currencyCode } }
                    totalDiscountSet { shopMoney { amount currencyCode } }
                  }
                }
                fulfillments {
                  id
                  status
                  createdAt
                  trackingInfo {
                    number
                    url
                  }
                }
                refunds {
                  id
                  createdAt
                }
              }
            }
        """.trimIndent()

         val CANCEL_ORDER_MUTATION = """
            mutation CancelOrder(${'$'}orderId: ID!) {
              orderCancel(orderId: ${'$'}orderId, reason: CUSTOMER, refund: false, restock: true) {
                job { id done }
                orderCancelUserErrors { field message }
                userErrors { field message }
              }
            }
        """.trimIndent()
    }
}
