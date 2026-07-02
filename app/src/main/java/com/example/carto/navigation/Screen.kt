package com.example.carto.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Saved : Screen("saved")
    data object Cart : Screen("cart")
    data object Account : Screen("account")
    data object AllProducts : Screen("all_products")
    data object AllVendors : Screen("all_vendors")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object ForgotPassword : Screen("forgot_password")
    data object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: Long) = "product_detail/$productId"
    }
    data object BrandProducts : Screen("brand_products/{brandName}") {
        fun createRoute(brandName: String) = "brand_products/${android.net.Uri.encode(brandName)}"
    }
    @Serializable
    data object Addresses : Screen("addresses")
    @Serializable
    data object NewAddress : Screen("new_address")
    @Serializable
    data object MapPicker : Screen("map_picker")
}

val bottomBarRoutes = setOf(
    Screen.Home.route,
    Screen.Saved.route,
    Screen.Cart.route,
    Screen.Account.route
)

val authRequiredRoutes = setOf(
    Screen.Saved.route,
    Screen.Cart.route
)