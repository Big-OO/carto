package com.example.carto.home.data


import com.example.carto.home.presentation.screens.model.AdUi

object HomeAdsFakeData {

    val ads = listOf(
        AdUi("1", "Big Sale ", "Up to 30% OFF", "Shop", 0xFF6A5AE0, 0xFF8E6CEF, "sale"),
        AdUi("2", "New Arrivals ", "Fresh products", "Explore", 0xFF00BFA6, 0xFF1DE9B6, "new"),
        AdUi("3", "Free Delivery ", "Orders above $50", "Grab", 0xFFFF6B6B, 0xFFFF8E53, "delivery")
    )
}