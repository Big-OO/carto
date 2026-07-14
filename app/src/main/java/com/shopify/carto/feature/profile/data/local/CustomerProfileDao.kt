package com.shopify.carto.feature.profile.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerProfileDao {
    @Query("SELECT * FROM customer_profiles WHERE id = :id LIMIT 1")
    fun observeCustomerProfile(id: String): Flow<CustomerProfileEntity?>

    @Query("SELECT * FROM customer_profiles WHERE id = :id LIMIT 1")
    suspend fun getCustomerProfile(id: String): CustomerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomerProfile(profile: CustomerProfileEntity)

    @Query("DELETE FROM customer_profiles WHERE id = :id")
    suspend fun deleteCustomerProfile(id: String)
}
