package com.shopify.carto.feature.search.data.remote.model

import com.google.gson.annotations.SerializedName

data class SearchCatalogImageDto(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("src")
    val src: String?,
)
