package com.shopify.carto.feature.home.data.mappers

import com.shopify.carto.feature.home.data.model.SmartCollectionDto
import com.shopify.carto.feature.home.domain.model.Brand

fun SmartCollectionDto.toDomain(): Brand {
    return Brand(
        id = this.id,
        name = this.title,
        imageUrl = this.image?.src,
        handle = this.handle
    )
}