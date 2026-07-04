package com.example.carto.feature.profile.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customer_profiles")
data class CustomerProfileEntity(
    @PrimaryKey
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String?,
    val ordersCount: Int,
    val totalSpent: String,
)
