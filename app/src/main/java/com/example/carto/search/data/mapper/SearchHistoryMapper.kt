package com.example.carto.search.data.mapper

import com.example.carto.search.data.local.SearchHistoryEntity
import com.example.carto.search.domain.model.SearchHistoryItem

fun SearchHistoryEntity.toDomain(): SearchHistoryItem {
    return SearchHistoryItem(
        id = id,
        query = query,
    )
}
