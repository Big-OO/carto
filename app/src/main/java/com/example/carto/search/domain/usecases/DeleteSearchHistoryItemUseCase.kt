package com.example.carto.search.domain.usecases

import com.example.carto.search.domain.model.SearchResult
import com.example.carto.search.domain.repository.SearchRepository
import javax.inject.Inject

class DeleteSearchHistoryItemUseCase @Inject constructor(
    private val repository: SearchRepository,
) {
    suspend operator fun invoke(id: Long): SearchResult<Unit> {
        return repository.deleteSearchHistoryItem(id)
    }
}
