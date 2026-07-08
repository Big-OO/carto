package com.shopify.carto.feature.home.data

import com.shopify.carto.R
import com.shopify.carto.feature.home.presentation.screens.model.AdUi

object HomeAdsFakeData {
    val ads = listOf(
        AdUi(
            id = "1",
            title = R.string.homeAdBigSaleTitle,
            subtitle = R.string.homeAdBigSaleSubtitle,
            buttonText = R.string.homeAdBigSaleButton,
            route = "sale"
        ),
        AdUi(
            id = "2",
            title = R.string.homeAdNewArrivalsTitle,
            subtitle = R.string.homeAdNewArrivalsSubtitle,
            buttonText = R.string.homeAdNewArrivalsButton,
            route = "new"
        ),
        AdUi(
            id = "3",
            title = R.string.homeAdFreeDeliveryTitle,
            subtitle = R.string.homeAdFreeDeliverySubtitle,
            buttonText = R.string.homeAdFreeDeliveryButton,
            route = "delivery"
        )
    )
}
