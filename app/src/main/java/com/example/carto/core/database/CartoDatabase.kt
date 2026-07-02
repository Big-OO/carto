package com.example.carto.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.carto.feature.search.data.local.SearchHistoryDao
import com.example.carto.feature.search.data.local.SearchHistoryEntity
import com.example.carto.feature.profile.data.local.CustomerProfileEntity
import com.example.carto.feature.profile.data.local.CustomerProfileDao

@Database(
    entities = [
        SearchHistoryEntity::class,
        CustomerProfileEntity::class
    ],
    version = 1,
    exportSchema = true,
)
abstract class CartoDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun customerProfileDao(): CustomerProfileDao

    companion object {
        const val DATABASE_NAME = "carto.db"
    }
}
