package com.example.carto.feature.search.data.remote.model

import com.google.gson.annotations.SerializedName

data class SearchVariantDto(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("price")
    val price: String?,
    @SerializedName("compare_at_price")
    val compareAtPrice: String?,
)
