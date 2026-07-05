package com.shopify.carto.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shopify.carto.feature.favorite.data.local.FavoriteProductDao
import com.shopify.carto.feature.favorite.data.local.FavoriteProductEntity
import com.shopify.carto.feature.profile.data.local.CustomerProfileDao
import com.shopify.carto.feature.profile.data.local.CustomerProfileEntity
import com.shopify.carto.feature.search.data.local.SearchHistoryDao
import com.shopify.carto.feature.search.data.local.SearchHistoryEntity

@Database(
    entities = [
        SearchHistoryEntity::class,
        CustomerProfileEntity::class,
        FavoriteProductEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class CartoDatabase : RoomDatabase() {

    abstract fun searchHistoryDao(): SearchHistoryDao

    abstract fun customerProfileDao(): CustomerProfileDao

    abstract fun favoriteProductDao(): FavoriteProductDao

    companion object {
        const val DATABASE_NAME = "carto.db"
    }
}