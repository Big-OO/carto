package com.example.carto.search.domain.usecases

import com.example.carto.search.domain.model.SearchResult
import com.example.carto.search.domain.repository.SearchRepository
import javax.inject.Inject

class SaveSearchQueryUseCase @Inject constructor(
    private val repository: SearchRepository,
) {
    suspend operator fun invoke(query: String): SearchResult<Unit> {
        return repository.saveSearchQuery(query.trim())
    }
}
