package com.shopify.carto.feature.search.domain.model

data class SearchCatalogProduct(
    val id: Long,
    val title: String,
    val price: Double,
    val compareAtPrice: Double?,
    val imageUrl: String?,
    val vendor: String,
    val productType: String,
)
