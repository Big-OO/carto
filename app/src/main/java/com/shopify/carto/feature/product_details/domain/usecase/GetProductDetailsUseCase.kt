package com.shopify.carto.feature.product_details.domain.usecase

import com.shopify.carto.feature.product_details.domain.repository.ProductDetailsRepository
import com.shopify.carto.feature.product_details.domain.model.Product
import javax.inject.Inject

class GetProductDetailsUseCase @Inject constructor(
    private val repository: ProductDetailsRepository
) {

    suspend operator fun invoke(productId: Long): Result<Product> {
        return repository.getProductDetails(productId)
    }
}