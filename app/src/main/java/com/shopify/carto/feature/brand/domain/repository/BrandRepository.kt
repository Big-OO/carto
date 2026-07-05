package com.shopify.carto.feature.brand.domain.repository

import com.shopify.carto.feature.brand.domain.model.Brand
import com.shopify.carto.feature.brand.domain.model.Product

interface BrandRepository {
    suspend fun getBrands(): Result<List<Brand>>
    suspend fun getProductsByBrand(vendor: String): Result<List<Product>>
}