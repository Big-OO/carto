package com.example.carto.feature.brand.data.remote.mapper

import com.example.carto.feature.brand.data.remote.dto.ProductDto
import com.example.carto.feature.brand.domain.model.Product

fun ProductDto.toDomain(): Product {
    return Product(
        id = id,
        title = title,
        imageUrl = images.firstOrNull()?.src.orEmpty(),
        productType = product_type,
        price = variants.firstOrNull()?.price.orEmpty()
    )
}