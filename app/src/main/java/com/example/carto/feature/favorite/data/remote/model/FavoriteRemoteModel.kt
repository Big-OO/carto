package com.example.carto.feature.favorite.data.remote.model

data class FavoriteRemoteModel(
    val productId: Long,
    val name: String,
    val imageUrl: String?,
    val price: Double,
    val addedAt: Long,
)
