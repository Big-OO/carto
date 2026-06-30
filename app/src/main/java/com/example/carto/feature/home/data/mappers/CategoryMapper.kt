package com.example.carto.feature.home.data.mappers

import com.example.carto.feature.home.domain.model.Category
import com.example.carto.feature.home.data.model.CollectionDto

fun CollectionDto.toCategory() = Category(
    id = id,
    title = title,
    imageUrl = image?.src,
    description = bodyHtml
)