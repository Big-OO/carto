package com.shopify.carto.feature.home.presentation.screens.model

data class AdUi(
    val id: String,
    val title: String,
    val subtitle: String,
    val buttonText: String,
    val route: String? = null,
)
