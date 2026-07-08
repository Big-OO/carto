package com.shopify.carto.feature.home.data.repository

import com.shopify.carto.feature.home.data.mappers.toDomain
import com.shopify.carto.feature.home.data.mappers.toCategory
import com.shopify.carto.feature.home.data.mappers.toProduct
import com.shopify.carto.feature.home.data.mappers.toCoupon
import com.shopify.carto.feature.home.data.remote.HomeRemoteDataSource
import com.shopify.carto.feature.home.domain.model.Brand
import com.shopify.carto.feature.home.domain.model.Category
import com.shopify.carto.feature.home.domain.model.Coupon
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

                response.products.map {
                    it.toProduct()
                }

            }

    }

    override suspend fun getCategories(): Result<List<Category>> {

        return remoteDataSource
            .getCollections()
            .map { response ->

                response.collections.map {

                    it.toCategory()

                }

            }

    }

    override suspend fun getBrands(): Result<List<Brand>> {

        return remoteDataSource
            .getBrands()
            .map { response ->

                response.smartCollections.map {

                    it.toDomain()

                }

            }

    }

    override suspend fun getProductsByCategory(
        collectionId: Long
    ): Result<List<Product>> {

        return remoteDataSource
            .getProductsByCollection(collectionId)
            .map { response ->

                response.products.map {

                    it.toProduct()

                }

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


    override suspend fun getCoupons(limit: Int): Result<List<Coupon>> {
        return remoteDataSource
            .getPriceRules(limit = limit)
            .map { response ->
                response.priceRules.map { it.toCoupon() }
            }
    }

}