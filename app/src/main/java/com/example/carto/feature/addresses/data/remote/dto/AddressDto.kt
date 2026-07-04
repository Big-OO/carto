package com.example.carto.feature.addresses.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AddressDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("address1") val address1: String? = null,
    @SerializedName("address2") val address2: String? = null,
    @SerializedName("city") val city: String? = null,
    @SerializedName("province") val province: String? = null,
    @SerializedName("country") val country: String? = null,
    @SerializedName("zip") val zip: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("default") val isDefault: Boolean? = null,
    @SerializedName("name") val name: String? = null,
)
