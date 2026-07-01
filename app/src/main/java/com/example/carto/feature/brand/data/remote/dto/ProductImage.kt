package com.example.carto.feature.brand.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProductImage(
    @SerializedName("id") val id: Long,
    @SerializedName("alt") val alt: String?,
    @SerializedName("src") val src: String,
    @SerializedName("height") val height: Int,
    @SerializedName("width") val width: Int
)