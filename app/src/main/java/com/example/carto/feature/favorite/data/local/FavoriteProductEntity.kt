package com.example.carto.feature.favorite.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_products")
data class FavoriteProductEntity(
    @PrimaryKey val productId: Long,
    val name: String,
    val imageUrl: String?,
    val price: Double,
    val addedAt: Long,
)