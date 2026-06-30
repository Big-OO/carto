package com.example.carto.feature.search.data.remote.model

import com.google.gson.annotations.SerializedName

data class SearchProductDto(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("handle")
    val handle: String?,
    @SerializedName("vendor")
    val vendor: String?,
    @SerializedName("product_type")
    val productType: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("tags")
    val tags: String?,
    @SerializedName("variants")
    val variants: List<SearchVariantDto>?,
    @SerializedName("images")
    val images: List<SearchImageDto>?,
)
