package com.example.carto.feature.search.data.mapper

import com.example.carto.feature.search.data.remote.model.SearchProductDto
import com.example.carto.feature.search.domain.model.SearchProduct

fun SearchProductDto.toDomain(): SearchProduct? {
    val productId = id ?: return null
    val productTitle = title?.takeIf { it.isNotBlank() } ?: return null
    val firstVariant = variants.orEmpty().firstOrNull()
    val productPrice = firstVariant?.price?.toDoubleOrNull() ?: 0.0

    return SearchProduct(
        id = productId,
        title = productTitle,
        price = productPrice,
        compareAtPrice = firstVariant?.compareAtPrice?.toDoubleOrNull(),
        imageUrl = images.orEmpty().firstOrNull()?.src,
        vendor = vendor.orEmpty(),
        productType = productType.orEmpty(),
    )
}

fun SearchProductDto.matchesKeyword(keyword: String): Boolean {
    val normalizedKeyword = keyword.trim()
    return listOf(
        title,
        vendor,
        productType,
        tags,
    ).any { value -> value?.contains(normalizedKeyword, ignoreCase = true) == true }
}
