package com.example.carto.feature.favorite.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

//@Dao
//interface FavoriteProductDao {
//    @Query("SELECT * FROM favorite_products ORDER BY addedAt DESC")
//    fun observeFavorites(): Flow<List<FavoriteProductEntity>>
//
//    @Query("SELECT productId FROM favorite_products")
//    fun observeFavoriteIds(): Flow<List<Long>>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(entity: FavoriteProductEntity)
//
//    @Query("DELETE FROM favorite_products WHERE productId = :productId")
//    suspend fun deleteById(productId: Long)
//
//    @Query("SELECT EXISTS(SELECT 1 FROM favorite_products WHERE productId = :productId)")
//    suspend fun exists(productId: Long): Boolean
//}
import androidx.room.Transaction


@Dao
interface FavoriteProductDao {

    @Query("SELECT * FROM favorite_products WHERE userId = :userId ORDER BY addedAt DESC")
    fun observeFavorites(userId: String): Flow<List<FavoriteProductEntity>>

    @Query("SELECT productId FROM favorite_products WHERE userId = :userId")
    fun observeFavoriteIds(userId: String): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavoriteProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<FavoriteProductEntity>)

    @Query("DELETE FROM favorite_products WHERE productId = :productId AND userId = :userId")
    suspend fun deleteById(productId: Long, userId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_products WHERE productId = :productId AND userId = :userId)")
    suspend fun exists(productId: Long, userId: String): Boolean

    @Query("SELECT * FROM favorite_products WHERE userId = :userId")
    suspend fun getAllOnce(userId: String): List<FavoriteProductEntity>

    @Query("DELETE FROM favorite_products WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)

    @Transaction
    suspend fun replaceForUser(userId: String, entities: List<FavoriteProductEntity>) {
        deleteAllForUser(userId)
        if (entities.isNotEmpty()) insertAll(entities)
    }
}
