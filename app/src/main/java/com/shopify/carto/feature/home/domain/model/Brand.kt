package com.shopify.carto.feature.home.domain.model

data class Brand(
    val id: Long,
    val name: String,
    val imageUrl: String?,
    val handle: String
)