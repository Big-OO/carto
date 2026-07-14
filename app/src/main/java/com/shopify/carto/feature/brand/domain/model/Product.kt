package com.shopify.carto.feature.brand.domain.model

data class Product(
    val id: Long,
    val title: String,
    val imageUrl: String,
    val productType: String,
    val price: String
)