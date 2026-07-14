package com.shopify.carto.feature.brand.data.remote.network

import com.shopify.carto.feature.brand.data.remote.dto.BrandProductsResponseDto
import com.shopify.carto.feature.brand.data.remote.dto.BrandsResponseDto
import retrofit2.Response

interface BrandNetworkDataSource {
    suspend fun getBrands(version: String): Response<BrandsResponseDto>

    suspend fun getProductsByBrand(
        version: String,
        vendor: String,
    ): Response<BrandProductsResponseDto>
}
