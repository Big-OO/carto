package com.shopify.carto.feature.brand.data.remote.network

import com.shopify.carto.feature.brand.data.remote.api.BrandsApiService
import com.shopify.carto.feature.brand.data.remote.dto.BrandProductsResponseDto
import com.shopify.carto.feature.brand.data.remote.dto.BrandsResponseDto
import retrofit2.Response
import javax.inject.Inject

class RetrofitBrandNetworkDataSource @Inject constructor(
    private val api: BrandsApiService,
) : BrandNetworkDataSource {

    override suspend fun getBrands(version: String): Response<BrandsResponseDto> {
        return api.getBrands(version = version)
    }

    override suspend fun getProductsByBrand(
        version: String,
        vendor: String,
    ): Response<BrandProductsResponseDto> {
        return api.getProductsByBrand(version = version, vendor = vendor)
    }
}
