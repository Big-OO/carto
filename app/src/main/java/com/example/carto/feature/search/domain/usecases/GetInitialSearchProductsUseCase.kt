package com.example.carto.feature.search.domain.usecases

import com.example.carto.feature.search.domain.model.SearchProduct
import com.example.carto.feature.search.domain.model.SearchResult
import com.example.carto.feature.search.domain.repository.SearchRepository
import javax.inject.Inject

class GetInitialSearchProductsUseCase @Inject constructor(
    private val repository: SearchRepository,
) {
    suspend operator fun invoke(): SearchResult<List<SearchProduct>> {
        return repository.getInitialProducts()
    }
}
