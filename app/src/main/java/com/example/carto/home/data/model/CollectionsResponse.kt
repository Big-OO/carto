package com.example.carto.home.data.model

import com.google.gson.annotations.SerializedName

data class CollectionsResponse(
    @SerializedName("custom_collections")
    val collections: List<CollectionDto>
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
    val image: CollectionImageDto?
)

data class CollectionImageDto(
    @SerializedName("src")
    val src: String,

    @SerializedName("alt")
    val alt: String?
)