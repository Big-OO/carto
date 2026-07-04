package com.shopify.carto.feature.addresses.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AddressResponseDto(
    @SerializedName("customer_address") val address: AddressDto? = null,
)
