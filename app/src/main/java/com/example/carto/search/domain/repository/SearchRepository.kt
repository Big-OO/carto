package com.example.carto.search.domain.repository

import com.example.carto.search.domain.model.SearchHistoryItem
import com.example.carto.search.domain.model.SearchProduct
import com.example.carto.search.domain.model.SearchResult
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun searchProducts(keyword: String): SearchResult<List<SearchProduct>>
    fun observeSearchHistory(): Flow<List<SearchHistoryItem>>
    suspend fun saveSearchQuery(query: String): SearchResult<Unit>
    suspend fun deleteSearchHistoryItem(id: Long): SearchResult<Unit>
    suspend fun clearSearchHistory(): SearchResult<Unit>
}
