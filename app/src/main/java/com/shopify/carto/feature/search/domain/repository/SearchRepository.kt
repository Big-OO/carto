package com.shopify.carto.feature.search.domain.repository

import com.shopify.carto.feature.search.domain.model.SearchCatalogProduct
import com.shopify.carto.feature.search.domain.model.SearchHistoryItem
import com.shopify.carto.feature.search.domain.model.SearchProduct
import com.shopify.carto.feature.search.domain.model.SearchResult
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun searchProducts(keyword: String): SearchResult<List<SearchProduct>>
    suspend fun getInitialCatalogProducts(): SearchResult<List<SearchCatalogProduct>>
    suspend fun searchCatalogProducts(keyword: String): SearchResult<List<SearchCatalogProduct>>
    fun observeSearchHistory(): Flow<List<SearchHistoryItem>>
    suspend fun saveSearchQuery(query: String): SearchResult<Unit>
    suspend fun deleteSearchHistoryItem(id: Long): SearchResult<Unit>
    suspend fun clearSearchHistory(): SearchResult<Unit>
}
