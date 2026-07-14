package com.shopify.carto.feature.search.data.remote.model

import com.google.gson.annotations.SerializedName

data class SearchCatalogVariantDto(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("price")
    val price: String?,
    @SerializedName("compare_at_price")
    val compareAtPrice: String?,
)
