package com.shopify.carto.feature.search.data.mapper

import com.shopify.carto.feature.search.data.remote.model.SearchProductSuggestionDto
import com.shopify.carto.feature.search.domain.model.SearchProduct

fun SearchProductSuggestionDto.toDomain(): SearchProduct? {
    val productId = id?.toShopifyProductLegacyId() ?: return null
    val productTitle = title?.takeIf { it.isNotBlank() } ?: return null

    return SearchProduct(
        id = productId,
        title = productTitle,
    )
}

private fun String.toShopifyProductLegacyId(): Long? {
    return substringAfterLast('/').toLongOrNull()
}
