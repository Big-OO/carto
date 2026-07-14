package com.shopify.carto.feature.search.data.remote.model

import com.google.gson.annotations.SerializedName

data class SearchCatalogProductsResponseDto(
    @SerializedName("products")
    val products: List<SearchCatalogProductDto>?,
)
