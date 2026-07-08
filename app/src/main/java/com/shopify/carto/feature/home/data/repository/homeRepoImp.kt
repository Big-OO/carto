package com.shopify.carto.feature.home.data.repository

import com.shopify.carto.feature.home.data.mappers.isDashboardBrand
import com.shopify.carto.feature.home.data.mappers.isDashboardCategory
import com.shopify.carto.feature.home.data.mappers.toBrand
import com.shopify.carto.feature.home.data.mappers.toCategory
import com.shopify.carto.feature.home.data.mappers.toProduct
import com.shopify.carto.feature.home.data.remote.HomeRemoteDataSource
import com.shopify.carto.feature.home.domain.model.Brand
import com.shopify.carto.feature.home.domain.model.Category
import com.shopify.carto.feature.home.domain.model.Product
import com.shopify.carto.feature.home.domain.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImp @Inject constructor(
    private val remoteDataSource: HomeRemoteDataSource
) : HomeRepository {

    override suspend fun getProducts(): Result<List<Product>> {
        return remoteDataSource
            .getProducts()
            .map { response ->
                response.products.map { it.toProduct() }
            }
    }

    override suspend fun getCategories(): Result<List<Category>> {
        return remoteDataSource
            .getCollections()
            .map { response ->
                response.collections
                    .filter { it.isDashboardCategory() }
                    .map { it.toCategory() }
                    .distinctBy { it.id }
                    .sortedWith(
                        compareBy<Category> { if (it.title.equals("New Arrivals", true)) 0 else 1 }
                            .thenBy { it.title.lowercase() }
                    )
            }
    }

    override suspend fun getBrands(): Result<List<Brand>> {
        return remoteDataSource
            .getBrands()
            .map { response ->
                response.collections
                    .filter { it.isDashboardBrand() }
                    .map { it.toBrand() }
                    .distinctBy { it.id }
                    .sortedBy { it.name.lowercase() }
            }
    }

    override suspend fun getProductsByCategory(
        collectionId: Long
    ): Result<List<Product>> {
        return remoteDataSource
            .getProductsByCollection(collectionId)
            .map { response ->
                response.products.map { it.toProduct() }
            }
    }

    override suspend fun getProductById(
        productId: Long
    ): Result<Product> {
        return remoteDataSource
            .getProductById(productId)
            .mapCatching { response ->
                response.product
                    ?.toProduct()
                    ?: throw Exception("Product was not found.")
            }
    }
}
