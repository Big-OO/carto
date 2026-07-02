package com.example.carto.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.carto.feature.search.data.local.SearchHistoryDao
import com.example.carto.feature.search.data.local.SearchHistoryEntity

//@Database(
//    entities = [SearchHistoryEntity::class],
//    version = 1,
//    exportSchema = true,
//)
//abstract class CartoDatabase : RoomDatabase() {
//    abstract fun searchHistoryDao(): SearchHistoryDao
//
//    companion object {
//        const val DATABASE_NAME = "carto.db"
//    }
//}

import com.example.carto.feature.favorite.data.local.FavoriteProductDao
import com.example.carto.feature.favorite.data.local.FavoriteProductEntity


@Database(
    entities = [SearchHistoryEntity::class, FavoriteProductEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class CartoDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun favoriteProductDao(): FavoriteProductDao

    companion object {
        const val DATABASE_NAME = "carto.db"
    }
}
