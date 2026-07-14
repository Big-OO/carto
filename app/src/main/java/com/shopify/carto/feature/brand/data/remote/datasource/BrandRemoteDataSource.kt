package com.shopify.carto.feature.brand.data.remote.datasource

import com.shopify.carto.core.network.config.ShopifyConfig
import com.shopify.carto.feature.brand.data.remote.network.BrandNetworkDataSource
import com.shopify.carto.feature.brand.data.remote.dto.BrandProductsResponseDto
import com.shopify.carto.feature.brand.data.remote.dto.BrandsResponseDto
import retrofit2.Response
import javax.inject.Inject

class BrandRemoteDataSource @Inject constructor(
    private val networkDataSource: BrandNetworkDataSource,
    private val shopifyConfig: ShopifyConfig
) {
    suspend fun getBrands(): Response<BrandsResponseDto> {
        return networkDataSource.getBrands(version = shopifyConfig.apiVersion)
    }

    suspend fun getProductsByBrand(vendor: String): Response<BrandProductsResponseDto> {
        return networkDataSource.getProductsByBrand(version = shopifyConfig.apiVersion, vendor = vendor)
    }
}