package com.shopify.carto.feature.payment.data.remote

interface OrderRemoteDataSource {
    suspend fun createOrder(version: String, request: CreateOrderGraphQlRequest): Result<CreatedOrderDto>
    suspend fun getPriceRules(version: String): Result<List<PriceRuleDto>>
    suspend fun getDiscountCodes(version: String, priceRuleId: Long): Result<List<DiscountCodeDto>>
    suspend fun lookupDiscountCode(version: String, code: String): Result<DiscountCodeDto>
}
