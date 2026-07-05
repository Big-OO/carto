package com.shopify.carto.feature.home.data.remote

import com.shopify.carto.feature.home.data.network.HomeNetworkDataSource
import javax.inject.Inject

class HomeRemoteDataSourceImpl @Inject constructor(
    private val networkDataSource: HomeNetworkDataSource
) : HomeRemoteDataSource {

    override suspend fun getProducts() =
        safeApiCall {
            networkDataSource.getProducts()
        }

    override suspend fun getCollections() =
        safeApiCall {
            networkDataSource.getCollections()
        }

    override suspend fun getBrands() =
        safeApiCall {
            networkDataSource.getBrands()
        }

    override suspend fun getProductsByCollection(
        collectionId: Long
    ) =
        safeApiCall {
            networkDataSource.getProductsByCollection(
                collectionId = collectionId
            )
        }

    override suspend fun getProductById(
        productId: Long
    ) =
        safeApiCall {
            networkDataSource.getProductById(
                productId = productId
            )
        }
}