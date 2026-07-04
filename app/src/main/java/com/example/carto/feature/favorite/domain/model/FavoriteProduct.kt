package com.example.carto.feature.favorite.domain.model


data class FavoriteProduct(
    val productId: Long,
    val name: String,
    val imageUrl: String?,
    val price: Double,
    val addedAt: Long,
)