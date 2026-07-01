package com.example.carto.feature.brand.data.remote.datasource

import com.example.carto.core.config.ShopifyConfig
import com.example.carto.feature.brand.data.remote.api.BrandsApiService
import com.example.carto.feature.brand.data.remote.dto.BrandProductsResponseDto
import com.example.carto.feature.brand.data.remote.dto.BrandsResponseDto
import retrofit2.Response
import javax.inject.Inject

class BrandRemoteDataSource @Inject constructor(
    private val brandApi: BrandsApiService,
    private val shopifyConfig: ShopifyConfig
) {
    suspend fun getBrands(): Response<BrandsResponseDto> {
        return brandApi.getBrands(version = shopifyConfig.apiVersion)
    }

    suspend fun getProductsByBrand(vendor: String): Response<BrandProductsResponseDto> {
        return brandApi.getProductsByBrand(version = shopifyConfig.apiVersion, vendor = vendor)
    }
}