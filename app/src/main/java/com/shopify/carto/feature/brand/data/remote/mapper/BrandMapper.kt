package com.shopify.carto.feature.brand.data.remote.mapper

import com.shopify.carto.feature.brand.data.remote.dto.BrandDto
import com.shopify.carto.feature.brand.domain.model.Brand

fun BrandDto.toDomain() = Brand(
    id = id,
    title = title,
    imageUrl = image?.src.orEmpty()
)