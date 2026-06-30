package com.example.carto.feature.search.data.repository

import com.example.carto.feature.search.data.local.SearchHistoryLocalDataSource
import com.example.carto.feature.search.data.mapper.toDomain
import com.example.carto.feature.search.data.remote.SearchProductRemoteDataSource
import com.example.carto.feature.search.data.result.SearchDataResult
import com.example.carto.feature.search.domain.model.SearchFailure
import com.example.carto.feature.search.domain.model.SearchFailureType
import com.example.carto.feature.search.domain.model.SearchHistoryItem
import com.example.carto.feature.search.domain.model.SearchProduct
import com.example.carto.feature.search.domain.model.SearchResult
import com.example.carto.feature.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val remoteDataSource: SearchProductRemoteDataSource,
    private val localDataSource: SearchHistoryLocalDataSource,
) : SearchRepository {
    override suspend fun searchProducts(keyword: String): SearchResult<List<SearchProduct>> {
        if (keyword.isBlank()) {
            return SearchResult.Success(emptyList())
        }

        return when (val result = remoteDataSource.searchProducts(keyword)) {
            is SearchDataResult.Success -> SearchResult.Success(result.data)
            is SearchDataResult.Failure -> result.failure.toDomainResult()
        }
    }

    override fun observeSearchHistory(): Flow<List<SearchHistoryItem>> {
        return localDataSource.observeHistory().map { history ->
            history.map { it.toDomain() }
        }
    }

    override suspend fun saveSearchQuery(query: String): SearchResult<Unit> {
        val cleanedQuery = query.trim()
        if (cleanedQuery.isBlank()) {
            return SearchResult.Success(Unit)
        }

        return when (val result = localDataSource.saveQuery(cleanedQuery)) {
            is SearchDataResult.Success -> SearchResult.Success(Unit)
            is SearchDataResult.Failure -> result.failure.toDomainResult()
        }
    }

    override suspend fun deleteSearchHistoryItem(id: Long): SearchResult<Unit> {
        return when (val result = localDataSource.deleteHistoryItem(id)) {
            is SearchDataResult.Success -> SearchResult.Success(Unit)
            is SearchDataResult.Failure -> result.failure.toDomainResult()
        }
    }

    override suspend fun clearSearchHistory(): SearchResult<Unit> {
        return when (val result = localDataSource.clearHistory()) {
            is SearchDataResult.Success -> SearchResult.Success(Unit)
            is SearchDataResult.Failure -> result.failure.toDomainResult()
        }
    }

    private fun SearchFailure.toDomainResult(): SearchResult.Failure {
        return SearchResult.Failure(this)
    }
}
