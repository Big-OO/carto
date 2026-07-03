package com.example.carto.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.carto.feature.search.data.local.SearchHistoryDao
import com.example.carto.feature.search.data.local.SearchHistoryEntity
import com.example.carto.feature.favorite.data.local.FavoriteProductDao
import com.example.carto.feature.favorite.data.local.FavoriteProductEntity
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(
    entities = [SearchHistoryEntity::class, FavoriteProductEntity::class],
    version = 3,
    exportSchema = true,
)
abstract class CartoDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun favoriteProductDao(): FavoriteProductDao

    companion object {
        const val DATABASE_NAME = "carto.db"
    }
}
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE favorite_products_new (
                productId INTEGER NOT NULL,
                userId TEXT NOT NULL,
                name TEXT NOT NULL,
                imageUrl TEXT,
                price REAL NOT NULL,
                addedAt INTEGER NOT NULL,
                PRIMARY KEY(productId, userId)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO favorite_products_new (productId, userId, name, imageUrl, price, addedAt)
            SELECT productId, 'guest', name, imageUrl, price, addedAt FROM favorite_products
            """.trimIndent()
        )
        db.execSQL("DROP TABLE favorite_products")
        db.execSQL("ALTER TABLE favorite_products_new RENAME TO favorite_products")
    }
}

