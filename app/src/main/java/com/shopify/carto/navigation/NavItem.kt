package com.shopify.carto.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val navItems = listOf(
    NavItem(Screen.Home.route, "Home", Icons.Filled.Home),
    NavItem(Screen.Saved.route, "Saved", Icons.Outlined.FavoriteBorder),
    NavItem(Screen.Cart.route, "Cart", Icons.Outlined.ShoppingCart),
    NavItem(Screen.Account.route, "Account", Icons.Outlined.AccountCircle)
)