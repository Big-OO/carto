package com.example.carto.search.domain.usecases

import com.example.carto.search.domain.model.SearchProduct
import com.example.carto.search.domain.model.SearchResult
import com.example.carto.search.domain.repository.SearchRepository
import javax.inject.Inject

class SearchProductsUseCase @Inject constructor(
    private val repository: SearchRepository,
) {
    suspend operator fun invoke(keyword: String): SearchResult<List<SearchProduct>> {
        return repository.searchProducts(keyword.trim())
    }
}
