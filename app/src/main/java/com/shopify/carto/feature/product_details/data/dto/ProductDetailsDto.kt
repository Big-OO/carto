package com.shopify.carto.feature.product_details.data.dto

import com.google.gson.annotations.SerializedName

data class ProductDetailsResponse(
    @SerializedName("product") val product: ProductDetailsDto
)

data class ProductDetailsDto(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("body_html") val bodyHtml: String?,
    @SerializedName("vendor") val vendor: String,
    @SerializedName("product_type") val productType: String,
    @SerializedName("variants") val variants: List<ProductVariantDto>,
    @SerializedName("options") val options: List<ProductOptionDto>,
    @SerializedName("images") val images: List<ProductImageDto>
)

data class ProductVariantDto(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("price") val price: String,
    @SerializedName("compare_at_price") val compareAtPrice: String?,
    @SerializedName("option1") val option1: String?,
    @SerializedName("option2") val option2: String?,
    @SerializedName("option3") val option3: String?,
    @SerializedName("inventory_quantity") val inventoryQuantity: Int
)

data class ProductOptionDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("position") val position: Int,
    @SerializedName("values") val values: List<String>
)

data class ProductImageDto(
    @SerializedName("id") val id: Long,
    @SerializedName("position") val position: Int,
    @SerializedName("src") val src: String
)