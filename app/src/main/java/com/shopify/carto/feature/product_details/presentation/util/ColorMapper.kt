package com.shopify.carto.feature.product_details.presentation.util

import androidx.compose.ui.graphics.Color

 fun colorFromName(name: String): Color {
    return when (name.trim().lowercase()) {
        "black" -> Color(0xFF1A1A1A)
        "white" -> Color(0xFFFFFFFF)
        "red" -> Color(0xFFE53935)
        "blue", "navy" -> Color(0xFF1E3A8A)
        "green" -> Color(0xFF2E7D32)
        "yellow" -> Color(0xFFFBC02D)
        "grey", "gray" -> Color(0xFF9E9E9E)
        "beige" -> Color(0xFFE8DCC4)
        "brown" -> Color(0xFF6D4C41)
        "pink" -> Color(0xFFEC407A)
        "orange" -> Color(0xFFFB8C00)
        "purple" -> Color(0xFF8E24AA)
        "teal" -> Color(0xFF00897B)
        else -> Color(0xFFBDBDBD)
    }
}