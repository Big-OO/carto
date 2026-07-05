package com.shopify.carto.feature.home.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.shopify.carto.feature.home.presentation.CategoryProductsViewModel
import com.shopify.carto.feature.home.presentation.HomeViewModel
import com.shopify.carto.feature.home.presentation.screens.AllBrandsScreen
import com.shopify.carto.feature.home.presentation.screens.AllCategoriesScreen
import com.shopify.carto.feature.home.presentation.screens.AllProductsScreen
import com.shopify.carto.feature.home.presentation.screens.CategoryProductsScreen
import com.shopify.carto.feature.home.presentation.screens.HomeScreen
import com.shopify.carto.feature.home.presentation.screens.productdetails.ProductDetailsScreen
import com.shopify.carto.feature.product_details.presentation.ProductDetailsScreen
import com.shopify.carto.navigation.Screen
import com.shopify.carto.navigation.viewmodel.AppSessionViewModel

fun NavGraphBuilder.homeGraph(navController: NavController) {
    composable(Screen.Home.route) {
        val viewModel: HomeViewModel = hiltViewModel()

        HomeScreen(
            viewModel = viewModel,
            onSeeAllProducts = {
                navController.navigate(HomeRoutes.AllProducts)
            },
            onSeeAllVendors = {
                navController.navigate(HomeRoutes.AllBrands)
            },
            onSeeAllCategories = {
                navController.navigate(HomeRoutes.AllCategories)
            },
            onProductClick = {
                navController.navigate(Screen.ProductDetail.createRoute(it.id))
            },
            onCategoryClick = {
                navController.navigate(HomeRoutes.categoryProducts(it.id, it.title))
            },
            onSearchClick = {
                navController.navigate(HomeRoutes.Search)
            },
            onBrandClick = { brandName ->
                navController.navigate(Screen.BrandProducts.createRoute(brandName))
            }
        )
    }
    composable(HomeRoutes.AllCategories) {

        val homeBackStackEntry = remember(it) {
            navController.getBackStackEntry(Screen.Home.route)
        }
        val viewModel: HomeViewModel = hiltViewModel(homeBackStackEntry)

        AllCategoriesScreen(
            viewModel = viewModel,

            onBackClick = {
                navController.popBackStack()
            },

            onCategoryClick = {

                navController.navigate(
                    HomeRoutes.categoryProducts(
                        it.id,
                        it.title
                    )
                )
            }
        )
    }

    composable(HomeRoutes.AllProducts) { backStackEntry ->
        val homeBackStackEntry = remember(backStackEntry) {
            navController.getBackStackEntry(Screen.Home.route)
        }
        val viewModel: HomeViewModel = hiltViewModel(homeBackStackEntry)
        val sessionViewModel: AppSessionViewModel = hiltViewModel()
        val session by sessionViewModel.session.collectAsStateWithLifecycle()
        val currentSession = session ?: return@composable

        AllProductsScreen(
            viewModel = viewModel,
            isGuest = currentSession.isGuest,
            onBackClick = { navController.popBackStack() },
            onProductClick = {
                navController.navigate(Screen.ProductDetail.createRoute(it.id))
            }
        )
    }

    composable(HomeRoutes.AllBrands) { backStackEntry ->
        val homeBackStackEntry = remember(backStackEntry) {
            navController.getBackStackEntry(Screen.Home.route)
        }
        val viewModel: HomeViewModel = hiltViewModel(homeBackStackEntry)

        AllBrandsScreen(
            viewModel = viewModel,
            onBackClick = { navController.popBackStack() },
            onBrandClick = { brandName ->
                navController.navigate(Screen.BrandProducts.createRoute(brandName))
            }
        )
    }

    composable(
        route = Screen.BrandProducts.route,
        arguments = listOf(
            navArgument("brandName") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val brandName = backStackEntry.arguments?.getString("brandName").orEmpty()
        com.shopify.carto.feature.brand.presentation.BrandScreen(
            brandId = brandName,
            onBackClick = { navController.popBackStack() },
            onProductClick = { productId ->
                navController.navigate(Screen.ProductDetail.createRoute(productId))
            }
        )
    }

    composable(
        route = HomeRoutes.CategoryProducts,
        arguments = listOf(
            navArgument("categoryId") { type = NavType.LongType },
            navArgument("categoryTitle") { type = NavType.StringType },
        )
    ) { backStackEntry ->
        val categoryId = backStackEntry.arguments?.getLong("categoryId") ?: return@composable
        val categoryTitle = backStackEntry.arguments?.getString("categoryTitle").orEmpty()
        val viewModel: CategoryProductsViewModel = hiltViewModel()
        val sessionViewModel: AppSessionViewModel = hiltViewModel()
        val session by sessionViewModel.session.collectAsStateWithLifecycle()
        val currentSession = session ?: return@composable

        LaunchedEffect(categoryId) {
            viewModel.loadCategory(categoryId)
        }

        CategoryProductsScreen(
            title = categoryTitle,
            viewModel = viewModel,
            isGuest = currentSession.isGuest,
            onBackClick = { navController.popBackStack() },
            onProductClick = {
                navController.navigate(Screen.ProductDetail.createRoute(it))
            }
        )
    }

}
