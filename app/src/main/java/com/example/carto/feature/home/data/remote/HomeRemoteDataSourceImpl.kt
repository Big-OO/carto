package com.example.carto.feature.home.data.remote

import com.example.carto.feature.home.data.HomeApiService
import jakarta.inject.Inject

class HomeRemoteDataSourceImpl @Inject constructor(
    private val api: HomeApiService
) : HomeRemoteDataSource {

    override suspend fun getProducts() =
        safeApiCall {
            api.getProducts()
        }

    override suspend fun getCollections() =
        safeApiCall {
            api.getCollections()
        }

    override suspend fun getBrands() =
        safeApiCall {
            api.getBrands()
        }

    override suspend fun getProductsByCollection(
        collectionId: Long
    ) =
        safeApiCall {
            api.getProductsByCollection(
                collectionId = collectionId
            )
        }

    override suspend fun getProductById(
        productId: Long
    ) =
        safeApiCall {
            api.getProductById(
                productId = productId
            )
        }
}