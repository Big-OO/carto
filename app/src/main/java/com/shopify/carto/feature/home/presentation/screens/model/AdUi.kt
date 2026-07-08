package com.shopify.carto.feature.home.presentation.screens.model

import androidx.annotation.StringRes

data class AdUi(
    val id: String,
    @StringRes val title: Int,
    @StringRes val subtitle: Int,
    @StringRes val buttonText: Int,
    val route: String? = null,
)
