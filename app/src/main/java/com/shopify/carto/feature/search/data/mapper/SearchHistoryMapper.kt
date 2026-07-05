package com.shopify.carto.feature.search.data.mapper

import com.shopify.carto.feature.search.data.local.SearchHistoryEntity
import com.shopify.carto.feature.search.domain.model.SearchHistoryItem

fun SearchHistoryEntity.toDomain(): SearchHistoryItem {
    return SearchHistoryItem(
        id = id,
        query = query,
    )
}
