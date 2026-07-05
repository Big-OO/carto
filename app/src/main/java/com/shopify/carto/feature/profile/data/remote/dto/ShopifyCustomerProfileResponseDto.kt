package com.shopify.carto.feature.profile.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ShopifyCustomerProfileResponseDto(
    val customer: ShopifyCustomerProfileDto?,
)

data class ShopifyCustomerProfileDto(
    val id: Long,
    val email: String?,
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("last_name")
    val lastName: String?,
    val phone: String?,
    @SerializedName("orders_count")
    val ordersCount: Int?,
    @SerializedName("total_spent")
    val totalSpent: String?,
)
