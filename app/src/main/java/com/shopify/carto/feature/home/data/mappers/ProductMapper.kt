package com.shopify.carto.feature.home.data.mappers

import com.shopify.carto.feature.home.data.model.ProductDto
import com.shopify.carto.feature.home.domain.model.Product

private const val LOW_STOCK_THRESHOLD = 5

fun ProductDto.toProduct(): Product {
    val firstVariant = variants?.firstOrNull()
    val price = firstVariant?.price?.toDoubleOrNull() ?: 0.0
    val compareAtPrice = firstVariant?.compareAtPrice?.toDoubleOrNull()
    val totalStock = variants?.sumOf { it.inventoryQuantity ?: 0 } ?: 0

    return Product(
        id = id,
        name = title,
        price = price,
        compareAtPrice = compareAtPrice?.takeIf { it > price },
        imageUrl = images.firstOrNull()?.src,
        imageCount = images.size,
        vendor = vendor,
        productType = productType,
        variantCount = variants?.size ?: 0,
        totalStock = totalStock,
        createdAt = createdAt,
        isNew = false,
        isOnSale = compareAtPrice != null && compareAtPrice > price,
        isLowStock = totalStock in 1..LOW_STOCK_THRESHOLD,
    )
}
