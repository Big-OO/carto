package com.shopify.carto.feature.search.domain.usecases

import com.shopify.carto.feature.search.domain.model.SearchResult
import com.shopify.carto.feature.search.domain.repository.SearchRepository
import javax.inject.Inject

class ClearSearchHistoryUseCase @Inject constructor(
    private val repository: SearchRepository,
) {
    suspend operator fun invoke(): SearchResult<Unit> {
        return repository.clearSearchHistory()
    }
}
