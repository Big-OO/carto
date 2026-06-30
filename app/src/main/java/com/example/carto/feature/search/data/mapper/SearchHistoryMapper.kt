package com.example.carto.feature.search.data.mapper

import com.example.carto.feature.search.data.local.SearchHistoryEntity
import com.example.carto.feature.search.domain.model.SearchHistoryItem

fun SearchHistoryEntity.toDomain(): SearchHistoryItem {
    return SearchHistoryItem(
        id = id,
        query = query,
    )
}
