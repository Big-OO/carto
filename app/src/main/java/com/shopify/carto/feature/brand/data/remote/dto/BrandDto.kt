package com.shopify.carto.feature.brand.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BrandDto(
    @SerializedName("id") val id: Long,
    @SerializedName("handle") val handle: String,
    @SerializedName("image") val image: BrandImage?,
    @SerializedName("title") val title: String,
)