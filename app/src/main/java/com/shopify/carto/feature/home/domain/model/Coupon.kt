package com.shopify.carto.feature.home.domain.model

data class Coupon(
    val id: Long,
    val code: String,
    val valueType: String,
    val value: Double,
    val usageLimit: Int?,
    val oncePerCustomer: Boolean,
    val startsAt: String?,
    val endsAt: String?,
) {
    val discountValue: Double
        get() = kotlin.math.abs(value)

    val isPercentage: Boolean
        get() = valueType == VALUE_TYPE_PERCENTAGE

    companion object {
        const val VALUE_TYPE_PERCENTAGE = "percentage"
    }
}
