package com.shopify.carto.feature.favorite.data.local

import androidx.room.Entity

@Entity(tableName = "favorite_products", primaryKeys = ["productId", "userId"])
data class FavoriteProductEntity(
    val productId: Long,
    val userId: String,
    val name: String,
    val imageUrl: String?,
    val price: Double,
    val addedAt: Long,
) {
    companion object {
        const val GUEST_USER_ID = "guest"
    }
}
