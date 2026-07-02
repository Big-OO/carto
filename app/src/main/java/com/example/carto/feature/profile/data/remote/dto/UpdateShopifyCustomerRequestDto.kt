package com.example.carto.feature.profile.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateShopifyCustomerRequestDto(
    val customer: UpdateShopifyCustomerDto,
)

data class UpdateShopifyCustomerDto(
    val id: Long,
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("last_name")
    val lastName: String?,
    val email: String?,
    val phone: String?,
)
