package com.example.carto.feature.brand.data.remote.repository

import com.example.carto.feature.brand.data.remote.datasource.BrandRemoteDataSource
import com.example.carto.feature.brand.data.remote.mapper.toDomain
import com.example.carto.feature.brand.domain.model.Brand
import com.example.carto.feature.brand.domain.model.Product
import com.example.carto.feature.brand.domain.repository.BrandRepository
import javax.inject.Inject

class BrandRepositoryImpl @Inject constructor(
    private val brandRemoteDataSource: BrandRemoteDataSource
) : BrandRepository {

    override suspend fun getBrands(): Result<List<Brand>> {
        return try {
            val response = brandRemoteDataSource.getBrands()
            if (response.isSuccessful) {
                val brands = response.body()?.smartCollections?.map { it.toDomain() } ?: emptyList()
                Result.success(brands)
            } else {
                Result.failure(Exception("Failed to fetch brands: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductsByBrand(vendor: String): Result<List<Product>> {
        return try {
            val response = brandRemoteDataSource.getProductsByBrand(vendor)
            if (response.isSuccessful) {
                val products = response.body()?.products?.map { it.toDomain() } ?: emptyList()
                Result.success(products)
            } else {
                Result.failure(Exception("Failed to fetch products: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}