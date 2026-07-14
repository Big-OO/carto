package com.shopify.carto.navigation

import androidx.compose.ui.graphics.painter.Painter

data class NavItem(
    val route: String,
    val label: String,
    val icon: Painter,
    val selectedIcon: Painter,
)

