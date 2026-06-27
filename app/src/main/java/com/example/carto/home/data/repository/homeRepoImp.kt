package com.example.carto.home.data.repository

import com.example.carto.home.data.network.ShopifyApi
import com.example.carto.home.domain.mappers.Product
import com.example.carto.home.domain.mappers.toProduct
import com.example.carto.home.domain.repository.HomeRepository


class HomeRepositoryImp(private val api: ShopifyApi) : HomeRepository {

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
}