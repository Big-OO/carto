package com.example.carto.home.domain.repository

import com.example.carto.home.domain.mappers.Product

interface HomeRepository {
    suspend fun getProducts(): Result<List<Product>>
}

