package com.example.carto.home.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.carto.home.domain.mappers.Product
import com.example.carto.home.domain.mappers.toProduct
import com.example.carto.home.domain.repository.HomeRepository
import com.example.carto.network.ShopifyApi

class HomeRepositoryImp(private val api: ShopifyApi) : HomeRepository {

    @RequiresApi(Build.VERSION_CODES.O)
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