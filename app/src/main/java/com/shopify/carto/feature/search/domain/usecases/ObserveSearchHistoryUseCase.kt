package com.shopify.carto.feature.search.domain.usecases

import com.shopify.carto.feature.search.domain.model.SearchHistoryItem
import com.shopify.carto.feature.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSearchHistoryUseCase @Inject constructor(
    private val repository: SearchRepository,
) {
    operator fun invoke(): Flow<List<SearchHistoryItem>> {
        return repository.observeSearchHistory()
    }
}
