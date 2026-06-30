package com.example.carto.home.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.carto.home.domain.model.Product
import com.example.carto.home.domain.mappers.toCategory
import com.example.carto.home.domain.mappers.toProduct
import com.example.carto.home.domain.model.Category
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


            Log.d("RAW", response.errorBody()?.string() ?: "No error")

            if (response.isSuccessful) {

                val dtoProducts = response.body()?.products.orEmpty()

                dtoProducts.forEach { dto ->
                    android.util.Log.d(
                        "DTO_DEBUG",
                        """
        Product: ${dto.title}
        variants = ${dto.variants}
        images = ${dto.images}
        """.trimIndent()
                    )
                }

                val products = dtoProducts.map { it.toProduct() }

                println("Mapped ${products.size} products")

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