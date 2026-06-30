package com.example.carto.search.domain.model

data class SearchProduct(
    val id: Long,
    val title: String,
    val price: Double,
    val compareAtPrice: Double?,
    val imageUrl: String?,
    val vendor: String,
    val productType: String,
)
