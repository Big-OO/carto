package com.example.carto.feature.addresses.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateAddressDto(
    @SerializedName("address1") val address1: String,
    @SerializedName("city") val city: String,
    @SerializedName("province") val province: String,
    @SerializedName("country") val country: String,
    @SerializedName("zip") val zip: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
)
