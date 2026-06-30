package com.example.carto.home.domain.repository

import com.example.carto.home.domain.model.Product
import com.example.carto.home.domain.model.Category

interface HomeRepository {
    suspend fun getProducts(): Result<List<Product>>
    suspend fun getCategories(): Result<List<Category>>

    suspend fun getProductsByCategory(
        collectionId: Long
    ): Result<List<Product>>
}

