package com.shopify.carto.feature.brand.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id") val id: Long,
    @SerializedName("images") val images: List<ProductImage>,
    @SerializedName("product_type") val product_type: String,
    @SerializedName("title") val title: String,
    @SerializedName("variants") val variants: List<VariantDto> = emptyList()
)