package com.example.carto.feature.brand.data.remote.mapper

import com.example.carto.feature.brand.data.remote.dto.BrandDto
import com.example.carto.feature.brand.domain.model.Brand

fun BrandDto.toDomain() = Brand(
    id = id,
    title = title,
    imageUrl = image?.src.orEmpty()
)