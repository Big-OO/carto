package com.example.carto.home.data.network.dtos

import com.google.gson.annotations.SerializedName

data class ProductsResponse(
    @SerializedName("products") val products: List<ProductDto>
)

data class ProductDto(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("handle") val handle: String,
    @SerializedName("vendor") val vendor: String,
    @SerializedName("product_type") val productType: String,
    @SerializedName("status") val status: String,
    @SerializedName("variants") val variants: List<VariantDto>,
    @SerializedName("images") val images: List<ImageDto>,
    @SerializedName("tags") val tags: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class VariantDto(
    @SerializedName("id") val id: Long,
    @SerializedName("price") val price: String
)

data class ImageDto(
    @SerializedName("id") val id: Long,
    @SerializedName("src") val src: String
)