package com.example.carto.feature.favorite.data.mapper

import com.example.carto.feature.favorite.data.local.FavoriteProductEntity
import com.example.carto.feature.favorite.domain.model.FavoriteProduct

fun FavoriteProductEntity.toDomain(): FavoriteProduct = FavoriteProduct(
    productId = productId,
    name = name,
    imageUrl = imageUrl,
    price = price,
    addedAt = addedAt,
)

fun FavoriteProduct.toEntity(): FavoriteProductEntity = FavoriteProductEntity(
    productId = productId,
    name = name,
    imageUrl = imageUrl,
    price = price,
    addedAt = addedAt,
)