package com.example.carto.feature.brand.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BrandImage(
    @SerializedName("alt") val alt: String?,
    @SerializedName("height") val height: Int,
    @SerializedName("src") val src: String,
    @SerializedName("width") val width: Int
)