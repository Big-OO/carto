package com.shopify.carto.feature.search.data.mapper

import com.shopify.carto.feature.search.data.remote.model.SearchCatalogProductDto
import com.shopify.carto.feature.search.domain.model.SearchCatalogProduct

fun SearchCatalogProductDto.toCatalogDomain(): SearchCatalogProduct? {
    val productId = id ?: return null
    val productTitle = title?.takeIf { it.isNotBlank() } ?: return null
    val firstVariant = variants.orEmpty().firstOrNull()
    val productPrice = firstVariant?.price?.toDoubleOrNull() ?: 0.0

    return SearchCatalogProduct(
        id = productId,
        title = productTitle,
        price = productPrice,
        compareAtPrice = firstVariant?.compareAtPrice?.toDoubleOrNull(),
        imageUrl = images.orEmpty().firstOrNull()?.src,
        vendor = vendor.orEmpty(),
        productType = productType.orEmpty(),
    )
}

fun SearchCatalogProductDto.matchesCatalogKeyword(keyword: String): Boolean {
    val normalizedKeyword = keyword.trim()
    if (normalizedKeyword.isBlank()) return true

    return listOf(
        title,
        vendor,
        productType,
        tags,
    ).any { value -> value?.contains(normalizedKeyword, ignoreCase = true) == true }
}
