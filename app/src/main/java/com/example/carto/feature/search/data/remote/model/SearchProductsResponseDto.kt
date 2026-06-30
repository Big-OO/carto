package com.example.carto.feature.search.data.remote.model

import com.google.gson.annotations.SerializedName

data class SearchProductsResponseDto(
    @SerializedName("products")
    val products: List<SearchProductDto>?,
)
