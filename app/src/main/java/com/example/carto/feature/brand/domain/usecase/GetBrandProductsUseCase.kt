package com.example.carto.feature.brand.domain.usecase

import com.example.carto.feature.brand.domain.model.Product
import com.example.carto.feature.brand.domain.repository.BrandRepository
import javax.inject.Inject

class GetBrandProductsUseCase @Inject constructor(
    private val repository: BrandRepository
) {
    suspend operator fun invoke(vendorName: String): Result<List<Product>> {
        return repository.getProductsByBrand(vendorName)
    }
}
