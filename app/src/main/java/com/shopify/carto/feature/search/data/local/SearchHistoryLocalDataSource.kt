package com.shopify.carto.feature.search.data.local

import com.shopify.carto.feature.search.data.result.SearchDataResult
import com.shopify.carto.feature.search.domain.model.SearchFailure
import com.shopify.carto.feature.search.domain.model.SearchFailureType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchHistoryLocalDataSource @Inject constructor(
    private val dao: SearchHistoryDao,
) {
    fun observeHistory(): Flow<List<SearchHistoryEntity>> {
        return dao.observeHistory().catch { emit(emptyList()) }
    }

    suspend fun saveQuery(query: String): SearchDataResult<Unit> {
        return runLocalOperation("save search query") {
            dao.upsertHistoryItem(
                SearchHistoryEntity(
                    query = query,
                    updatedAt = System.currentTimeMillis(),
                )
            )
        }
    }

    suspend fun deleteHistoryItem(id: Long): SearchDataResult<Unit> {
        return runLocalOperation("delete search history item") {
            dao.deleteHistoryItem(id)
        }
    }

    suspend fun clearHistory(): SearchDataResult<Unit> {
        return runLocalOperation("clear search history") {
            dao.clearHistory()
        }
    }

    private suspend fun runLocalOperation(
        operationName: String,
        operation: suspend () -> Unit,
    ): SearchDataResult<Unit> {
        return try {
            operation()
            SearchDataResult.Success(Unit)
        } catch (exception: Exception) {
            SearchDataResult.Failure(
                SearchFailure(
                    type = SearchFailureType.LocalStorage,
                    developerMessage = "Failed to $operationName: ${exception::class.java.name}. ${exception.message.orEmpty().ifBlank { "No message provided." }}",
                )
            )
        }
    }
}
