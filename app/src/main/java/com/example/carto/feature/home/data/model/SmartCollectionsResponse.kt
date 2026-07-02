package com.example.carto.feature.home.data.model

import com.google.gson.annotations.SerializedName

data class SmartCollectionsResponse(
    @SerializedName("smart_collections")
    val smartCollections: List<SmartCollectionDto>
)

data class SmartCollectionDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("title")
    val title: String,

    @SerializedName("handle")
    val handle: String,

    @SerializedName("body_html")
    val bodyHtml: String?,

    @SerializedName("image")
    val image: BrandImageDto?
)

data class BrandImageDto(
    @SerializedName("src")
    val src: String
)