package com.example.carto.home.domain.mappers

import com.example.carto.home.data.network.dtos.ProductDto

data class Product(
    val id: Long,
    val name: String,
    val price: Double,
    val imageUrl: String?
)

fun ProductDto.toProduct(): Product = Product(
    id = id,
    name = title,
    price = variants.firstOrNull()?.price?.toDoubleOrNull() ?: 0.0,
    imageUrl = images.firstOrNull()?.src
)