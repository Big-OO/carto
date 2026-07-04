package com.shopify.carto.feature.home.domain.model


data class Category(
    val id: Long,
    val title: String,
    val imageUrl: String?,
    val description: String?
)
