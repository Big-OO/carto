package com.shopify.carto.feature.search.domain.usecases

import com.shopify.carto.feature.search.domain.model.SearchCatalogProduct
import com.shopify.carto.feature.search.domain.model.SearchResult
import com.shopify.carto.feature.search.domain.repository.SearchRepository
import javax.inject.Inject

class GetInitialSearchCatalogProductsUseCase @Inject constructor(
    private val repository: SearchRepository,
) {
    suspend operator fun invoke(): SearchResult<List<SearchCatalogProduct>> {
        return repository.getInitialCatalogProducts()
    }
}
