package com.example.carto.feature.brand.domain.usecase

import com.example.carto.feature.brand.domain.model.Brand
import com.example.carto.feature.brand.domain.repository.BrandRepository
import javax.inject.Inject

class GetBrandDetailsUseCase @Inject constructor(
    private val repository: BrandRepository
) {
    suspend operator fun invoke(vendorName: String): Result<Brand> {
        return repository.getBrands().mapCatching { brands ->
            brands.find { it.title.equals(vendorName, ignoreCase = true) }
                ?: Brand(id = 0L, title = vendorName, imageUrl = "")
        }
    }
}
