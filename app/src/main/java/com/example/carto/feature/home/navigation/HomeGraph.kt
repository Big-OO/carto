package com.example.carto.feature.home.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.carto.feature.home.presentation.CategoryProductsViewModel
import com.example.carto.feature.home.presentation.HomeUiState
import com.example.carto.feature.home.presentation.HomeViewModel
import com.example.carto.feature.home.presentation.screens.AllBrandsScreen
import com.example.carto.feature.home.presentation.screens.AllCategoriesScreen
import com.example.carto.feature.home.presentation.screens.AllProductsScreen
import com.example.carto.feature.home.presentation.screens.CategoryProductsScreen
import com.example.carto.feature.home.presentation.screens.HomeScreen
import com.example.carto.feature.home.presentation.screens.productdetails.ProductDetailsScreen
import com.example.carto.navigation.Screen
import com.example.carto.navigation.viewmodel.AppSessionViewModel

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
            }
        )
    }
    composable(HomeRoutes.AllCategories) {

        val viewModel: HomeViewModel = hiltViewModel()

        val state by viewModel.uiState.collectAsState()

        AllCategoriesScreen(
            categories =
                (state as? HomeUiState.Success)
                    ?.content
                    ?.categories
                    ?: emptyList(),

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

    composable(HomeRoutes.AllProducts) {
        val viewModel: HomeViewModel = hiltViewModel()
        val state by viewModel.uiState.collectAsState()
        val sessionViewModel: AppSessionViewModel = hiltViewModel()
        val session by sessionViewModel.session.collectAsStateWithLifecycle()

        AllProductsScreen(
            products = (state as? HomeUiState.Success)?.content?.products ?: emptyList(),
            isGuest = session.isGuest,
            onBackClick = { navController.popBackStack() },
            onProductClick = {
                navController.navigate(Screen.ProductDetail.createRoute(it.id))
            }
        )
    }

    composable(HomeRoutes.AllBrands) {
        val viewModel: HomeViewModel = hiltViewModel()

        AllBrandsScreen(
            viewModel = viewModel,
            onBackClick = { navController.popBackStack() }
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

        LaunchedEffect(categoryId) {
            viewModel.loadCategory(categoryId)
        }

        CategoryProductsScreen(
            title = categoryTitle,
            viewModel = viewModel,
            isGuest = session.isGuest,
            onBackClick = { navController.popBackStack() },
            onProductClick = {
                navController.navigate(Screen.ProductDetail.createRoute(it))
            }
        )
    }

    composable(
        route = Screen.ProductDetail.route,
        arguments = listOf(
            navArgument("productId") { type = NavType.LongType }
        )
    ) {
        ProductDetailsScreen(
            onBackClick = { navController.popBackStack() }
        )
    }
}
