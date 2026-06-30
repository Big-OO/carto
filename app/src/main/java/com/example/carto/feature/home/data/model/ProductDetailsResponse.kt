package com.example.carto.feature.home.data.model

import com.google.gson.annotations.SerializedName

data class ProductDetailsResponse(
    @SerializedName("product") val product: ProductDto?,
)
