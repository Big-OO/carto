package com.example.carto.home.domain.repository


interface HomeRepository {
    suspend fun getProducts(): Result<List<com.example.carto.home.domain.mappers.Product>>
}

