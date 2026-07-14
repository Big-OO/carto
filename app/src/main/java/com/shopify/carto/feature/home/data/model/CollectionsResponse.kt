package com.shopify.carto.feature.home.data.model

import com.google.gson.annotations.SerializedName

data class CollectionsResponse(
    @SerializedName("custom_collections")
    val collections: List<CollectionDto> = emptyList()
)

data class CollectionDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("title")
    val title: String,

    @SerializedName("handle")
    val handle: String,

    @SerializedName("body_html")
    val bodyHtml: String?,

    @SerializedName("image")
    val image: CollectionImageDto?,

    @SerializedName("template_suffix")
    val templateSuffix: String?,

    @SerializedName("published_at")
    val publishedAt: String?,

    @SerializedName("published_scope")
    val publishedScope: String?,

    @SerializedName("admin_graphql_api_id")
    val adminGraphqlApiId: String?,

    @SerializedName("updated_at")
    val updatedAt: String?
)

data class CollectionImageDto(
    @SerializedName("src")
    val src: String?,

    @SerializedName("alt")
    val alt: String?
)
