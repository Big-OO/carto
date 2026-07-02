package com.example.carto.feature.home.data.mappers

import com.example.carto.feature.home.data.model.SmartCollectionDto
import com.example.carto.feature.home.domain.model.Brand

fun SmartCollectionDto.toDomain(): Brand {
    return Brand(
        id = this.id,
        name = this.title,
        imageUrl = this.image?.src,
        handle = this.handle
    )
}