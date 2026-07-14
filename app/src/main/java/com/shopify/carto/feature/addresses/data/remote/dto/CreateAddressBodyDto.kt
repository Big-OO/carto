package com.shopify.carto.feature.addresses.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateAddressBodyDto(
    @SerializedName("address") val address: CreateAddressDto,
)
