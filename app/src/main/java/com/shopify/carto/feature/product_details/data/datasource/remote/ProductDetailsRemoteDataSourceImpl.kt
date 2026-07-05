package com.shopify.carto.feature.product_details.data.datasource.remote

import com.shopify.carto.core.common.exception.DataException
import com.shopify.carto.feature.product_details.data.dto.ProductDetailsResponse
import com.shopify.carto.feature.product_details.data.mapper.toDomainException
import com.shopify.carto.feature.product_details.data.service.ProductDetailsService
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ProductDetailsRemoteDataSourceImpl @Inject constructor(
    private val service: ProductDetailsService
) : ProductDetailsRemoteDataSource {

    override suspend fun getProductDetails(productId: Long): Result<ProductDetailsResponse> {
        return try {
            val response = service.getProductDetails(productId = productId)
            Result.success(response)
        } catch (exception: HttpException) {
            Result.failure(exception.toDomainException(productId))
        } catch (exception: IOException) {
            Result.failure(DataException.Network(exception))
        } catch (exception: Exception) {
            Result.failure(DataException.Unknown(exception))
        }
    }
}