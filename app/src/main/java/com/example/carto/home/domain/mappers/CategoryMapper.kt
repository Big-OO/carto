package com.example.carto.home.domain.mappers

import com.example.carto.home.domain.model.Category
import com.example.carto.network.model.CollectionDto

fun CollectionDto.toCategory() = Category(
    id = id,
    title = title,
    imageUrl = image?.src,
    description = bodyHtml
)