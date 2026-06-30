package com.example.carto.home.data.mappers

import com.example.carto.home.domain.model.Category
import com.example.carto.home.data.model.CollectionDto

fun CollectionDto.toCategory() = Category(
    id = id,
    title = title,
    imageUrl = image?.src,
    description = bodyHtml
)