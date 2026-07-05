package com.shopify.carto.feature.home.data.mappers

import com.shopify.carto.feature.home.domain.model.Category
import com.shopify.carto.feature.home.data.model.CollectionDto

fun CollectionDto.toCategory() = Category(
    id = id,
    title = title,
    imageUrl = image?.src,
    description = bodyHtml
)