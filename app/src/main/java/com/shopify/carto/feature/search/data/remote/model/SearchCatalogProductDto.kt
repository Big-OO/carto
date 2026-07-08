package com.shopify.carto.feature.search.data.remote.model

import com.google.gson.annotations.SerializedName

data class SearchCatalogProductDto(
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
    val variants: List<SearchCatalogVariantDto>?,
    @SerializedName("images")
    val images: List<SearchCatalogImageDto>?,
)
