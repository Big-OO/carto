package com.shopify.carto.feature.payment.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderApiService {
    @POST("admin/api/{version}/graphql.json")
    suspend fun createOrder(
        @Path("version") version: String,
        @Body request: CreateOrderGraphQlRequest
    ): Response<CreateOrderGraphQlResponse>

    @GET("admin/api/{version}/price_rules.json")
    suspend fun getPriceRules(
        @Path("version") version: String
    ): Response<PriceRulesListResponseDto>

    @GET("admin/api/{version}/price_rules/{priceRuleId}/discount_codes.json")
    suspend fun getDiscountCodes(
        @Path("version") version: String,
        @Path("priceRuleId") priceRuleId: Long
    ): Response<DiscountCodesListResponseDto>

    @GET("admin/api/{version}/discount_codes/lookup.json")
    suspend fun lookupDiscountCode(
        @Path("version") version: String,
        @Query("code") code: String
    ): Response<DiscountCodeResponseDto>
}

data class CreateOrderGraphQlRequest(
    val query: String,
    val variables: CreateOrderVariables
)

data class CreateOrderVariables(
    val order: OrderInput,
    val options: OrderOptionsInput = OrderOptionsInput()
)

data class OrderOptionsInput(
    val inventoryBehaviour: String = "DECREMENT_OBEYING_POLICY",
    val sendReceipt: Boolean = true,
    val sendFulfillmentReceipt: Boolean = false
)

data class OrderInput(
    val currency: String = "USD",
    val email: String,
    val phone: String? = null,
    val customer: CustomerAssociateInput? = null,
    val lineItems: List<LineItemInput>,
    val shippingAddress: ShippingAddressInput? = null,
    val shippingLines: List<ShippingLineInput> = emptyList(),
    val discountCode: DiscountCodeInput? = null,
    val financialStatus: String,
    val transactions: List<TransactionInput> = emptyList()
)

data class CustomerAssociateInput(
    val toAssociate: CustomerIdInput
)

data class CustomerIdInput(
    val id: String
)

data class LineItemInput(
    val variantId: String,
    val quantity: Int,
    val priceSet: PriceSetInput? = null,
)

data class ShippingAddressInput(
    val firstName: String,
    val lastName: String,
    val company: String? = null,
    val address1: String,
    val address2: String? = null,
    val city: String,
    val provinceCode: String? = null,
    val countryCode: String? = null,
    val zip: String? = null,
    val phone: String? = null
)

data class ShippingLineInput(
    val title: String = "Standard",
    val code: String = "Standard",
    val source: String = "shopify",
    val priceSet: PriceSetInput
)

data class PriceSetInput(
    val shopMoney: MoneyInput,
    val presentmentMoney: MoneyInput? = null,
)

data class MoneyInput(
    val amount: String,
    val currencyCode: String
)

data class DiscountCodeInput(
    val itemFixedDiscountCode: ItemFixedDiscountInput? = null
)

data class ItemFixedDiscountInput(
    val code: String,
    val amountSet: PriceSetInput
)

data class TransactionInput(
    val kind: String = "SALE",
    val status: String = "SUCCESS",
    val gateway: String = "manual",
    val amountSet: PriceSetInput
)

data class CreateOrderGraphQlResponse(
    val data: CreateOrderDataDto?
)

data class CreateOrderDataDto(
    val orderCreate: OrderCreatePayloadDto?
)

data class OrderCreatePayloadDto(
    val order: CreatedOrderDto?,
    val userErrors: List<UserErrorDto>?
)

data class CreatedOrderDto(
    val id: String?,
    val name: String?
)

data class UserErrorDto(
    val field: List<String>?,
    val message: String?
)

data class PriceRulesListResponseDto(
    val price_rules: List<PriceRuleDto>?
)

data class PriceRuleDto(
    val id: Long?,
    val value_type: String?,
    val value: String?,
    val title: String?
)

data class DiscountCodesListResponseDto(
    val discount_codes: List<DiscountCodeDto>?
)

data class DiscountCodeResponseDto(
    val discount_code: DiscountCodeDto?
)

data class DiscountCodeDto(
    val id: Long?,
    val price_rule_id: Long?,
    val code: String?
)
