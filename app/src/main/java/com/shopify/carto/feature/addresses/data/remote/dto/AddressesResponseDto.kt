package com.shopify.carto.feature.addresses.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AddressesResponseDto(
    @SerializedName("addresses") val addresses: List<AddressDto> = emptyList(),
)
