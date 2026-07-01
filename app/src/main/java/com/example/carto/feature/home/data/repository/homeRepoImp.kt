package com.example.carto.feature.home.data.repository

import com.example.carto.feature.home.data.HomeApiService
import com.example.carto.feature.home.data.mappers.toBrand
import com.example.carto.feature.home.data.mappers.toCategory
import com.example.carto.feature.home.data.mappers.toProduct
import com.example.carto.feature.home.domain.model.Brand
import com.example.carto.feature.home.domain.model.Category
import com.example.carto.feature.home.domain.model.Product
import com.example.carto.feature.home.domain.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImp @Inject constructor(
    private val api: HomeApiService,
) : HomeRepository {

    override suspend fun getProducts(): Result<List<Product>> {
        return try {
            val response = api.getProducts()
            if (response.isSuccessful) {
                val products = response.body()?.products.orEmpty().map { it.toProduct() }
                Result.success(products)
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCategories(): Result<List<Category>> {
        return try {
            val response = api.getCollections()
            if (response.isSuccessful) {
                val categories = response.body()?.collections.orEmpty().map { it.toCategory() }
                Result.success(categories)
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductById(productId: Long): Result<Product> {
        return try {
            val response = api.getProductById(productId = productId)
            if (response.isSuccessful) {
                val product = response.body()?.product?.toProduct()
                if (product != null) {
                    Result.success(product)
                } else {
                    Result.failure(Exception("Product was not found."))
                }
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductsByCategory(collectionId: Long): Result<List<Product>> {
        return try {
            val response = api.getProductsByCollection(collectionId = collectionId)
            if (response.isSuccessful) {
                val products = response.body()?.products.orEmpty().map { it.toProduct() }
                Result.success(products)
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBrands(): Result<List<Brand>> {
        return try {
            val response = api.getBrands()
            if (response.isSuccessful) {
                val brands = response.body()?.smartCollections.orEmpty().map { it.toBrand() }
                Result.success(brands)
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
