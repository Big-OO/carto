package com.example.carto.feature.favorite.data.mapper

import com.example.carto.feature.favorite.data.local.FavoriteProductEntity
import com.example.carto.feature.favorite.domain.model.FavoriteProduct
import com.example.carto.feature.favorite.data.remote.model.FavoriteRemoteModel


fun FavoriteProductEntity.toDomain(): FavoriteProduct = FavoriteProduct(
    productId = productId,
    name = name,
    imageUrl = imageUrl,
    price = price,
    addedAt = addedAt,
)

fun FavoriteProduct.toEntity(userId: String): FavoriteProductEntity = FavoriteProductEntity(
    productId = productId,
    userId = userId,
    name = name,
    imageUrl = imageUrl,
    price = price,
    addedAt = addedAt,
)

fun FavoriteProduct.toRemoteModel(): FavoriteRemoteModel = FavoriteRemoteModel(
    productId = productId,
    name = name,
    imageUrl = imageUrl,
    price = price,
    addedAt = addedAt,
)

fun FavoriteProductEntity.toRemoteModel(): FavoriteRemoteModel = FavoriteRemoteModel(
    productId = productId,
    name = name,
    imageUrl = imageUrl,
    price = price,
    addedAt = addedAt,
)

fun FavoriteRemoteModel.toEntity(userId: String): FavoriteProductEntity = FavoriteProductEntity(
    productId = productId,
    userId = userId,
    name = name,
    imageUrl = imageUrl,
    price = price,
    addedAt = addedAt,
)
