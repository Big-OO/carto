package com.shopify.carto.feature.brand.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BrandsResponseDto(
    @SerializedName("smart_collections")
    val smartCollections: List<BrandDto>
)
