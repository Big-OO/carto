package com.shopify.carto.feature.search.domain.usecases

import com.shopify.carto.feature.search.domain.model.SearchCatalogProduct
import com.shopify.carto.feature.search.domain.model.SearchResult
import com.shopify.carto.feature.search.domain.repository.SearchRepository
import javax.inject.Inject

class SearchCatalogProductsUseCase @Inject constructor(
    private val repository: SearchRepository,
) {
    suspend operator fun invoke(keyword: String): SearchResult<List<SearchCatalogProduct>> {
        return repository.searchCatalogProducts(keyword.trim())
    }
}
