package com.shopify.carto.feature.payment.data.remote

import javax.inject.Inject

class OrderRemoteDataSourceImpl @Inject constructor(
    private val apiService: OrderApiService,
) : OrderRemoteDataSource {

    override suspend fun createOrder(
        version: String,
        request: CreateOrderGraphQlRequest,
    ): Result<CreatedOrderDto> {
        return try {
            val response = apiService.createOrder(version, request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val errors = body.errors
                if (!errors.isNullOrEmpty()) {
                    val errorMsg = errors.joinToString(", ") { it.message ?: "Unknown error" }
                    Result.failure(Exception(errorMsg))
                } else {
                    val payload = body.data?.orderCreate
                    val userErrors = payload?.userErrors
                    if (!userErrors.isNullOrEmpty()) {
                        val errorMsg = userErrors.joinToString(", ") { it.message ?: "Unknown error" }
                        Result.failure(Exception(errorMsg))
                    } else if (payload?.order != null) {
                        Result.success(payload.order)
                    } else {
                        Result.failure(Exception("Order creation failed: empty order response"))
                    }
                }
            } else {
                Result.failure(Exception("HTTP error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPriceRules(version: String): Result<List<PriceRuleDto>> {
        return try {
            val response = apiService.getPriceRules(version)
            if (response.isSuccessful) {
                Result.success(response.body()?.price_rules ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch price rules: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDiscountCodes(
        version: String,
        priceRuleId: Long,
    ): Result<List<DiscountCodeDto>> {
        return try {
            val response = apiService.getDiscountCodes(version, priceRuleId)
            if (response.isSuccessful) {
                Result.success(response.body()?.discount_codes ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch discount codes: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun lookupDiscountCode(version: String, code: String): Result<DiscountCodeDto> {
        return try {
            val response = apiService.lookupDiscountCode(version, code)
            if (response.isSuccessful && response.body()?.discount_code != null) {
                Result.success(response.body()!!.discount_code!!)
            } else {
                Result.failure(Exception("Discount code not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
