package com.shopify.carto.feature.brand.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BrandProductsResponseDto(
    @SerializedName("products")
    val products: List<ProductDto>
)