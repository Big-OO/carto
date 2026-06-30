package com.example.carto.home.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.carto.home.domain.model.Product
import com.example.carto.home.data.mappers.toCategory
import com.example.carto.home.data.mappers.toProduct
import com.example.carto.home.domain.model.Category
import com.example.carto.home.domain.repository.HomeRepository
import com.example.carto.home.data.HomeApiService

class HomeRepositoryImp(private val api: HomeApiService) : HomeRepository {

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
    override suspend fun getCategories(): Result<List<Category>> {
        return try {
            val response = api.getCollections()

            if (response.isSuccessful) {
                val categories = response.body()
                    ?.collections
                    .orEmpty()
                    .map { it.toCategory() }

                Result.success(categories)
            } else {
                Result.failure(
                    Exception("HTTP ${response.code()}: ${response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getProductsByCategory(
        collectionId: Long
    ): Result<List<Product>> {

        return try {

            val response = api.getProductsByCollection(collectionId = collectionId)




            if (response.isSuccessful) {

                val dtoProducts = response.body()?.products.orEmpty()



                val products = dtoProducts.map { it.toProduct() }


                Result.success(products)

            } else {

                Result.failure(Exception("HTTP ${response.code()}"))

            }

        } catch (e: Exception) {

            e.printStackTrace()

            Result.failure(e)

        }
    }
}