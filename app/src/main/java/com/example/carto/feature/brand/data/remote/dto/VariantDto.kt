package com.example.carto.feature.brand.data.remote.dto

import com.google.gson.annotations.SerializedName

data class VariantDto(
    @SerializedName("id") val id: Long,
    @SerializedName("price") val price: String,
    @SerializedName("compare_at_price") val compareAtPrice: String?,
    @SerializedName("inventory_quantity") val inventoryQuantity: Int?
)
