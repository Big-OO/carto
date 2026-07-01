package com.example.carto.feature.brand.domain.usecase

import com.example.carto.feature.brand.domain.model.Brand
import com.example.carto.feature.brand.domain.repository.BrandRepository
import javax.inject.Inject

class GetBrandsUseCase @Inject constructor(
    private val repository: BrandRepository
) {
    suspend operator fun invoke(): Result<List<Brand>> {
        return repository.getBrands()
    }
}
