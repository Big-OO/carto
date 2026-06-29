package com.example.carto.home.navigation


import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.carto.home.data.repository.HomeRepositoryImp
import com.example.carto.home.presentation.HomeUiState
import com.example.carto.home.presentation.HomeViewModel
import com.example.carto.home.presentation.HomeViewModelFactory
import com.example.carto.home.presentation.screens.AllProductsScreen
import com.example.carto.home.presentation.screens.HomeScreen
import com.example.carto.home.presentation.screens.AllBrandsScreen
import com.example.carto.navigation.Screen
import com.example.carto.network.RetrofitProvider

fun NavGraphBuilder.homeGraph(
    navController: NavController
) {

    composable(Screen.Home.route) {

        val repository = remember {
            val api = RetrofitProvider.create(
                hostName = "mad46-and7.myshopify.com",
                accessToken = "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"
            )
            HomeRepositoryImp(api)
        }

        val viewModel: HomeViewModel =
            viewModel(factory = HomeViewModelFactory(repository))

        HomeScreen(
            viewModel = viewModel,
            onSeeAllProducts = {
                navController.navigate(HomeRoutes.AllProducts)
            },
            onSeeAllVendors = {
                navController.navigate(HomeRoutes.AllVendors)
            },
            onProductClick = {
                navController.navigate(HomeRoutes.productDetails(it.id))
            }
        )
    }

    composable(HomeRoutes.AllProducts) {

        val repository = remember {
            val api = RetrofitProvider.create(
                hostName = "mad46-and7.myshopify.com",
                accessToken = "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"
            )
            HomeRepositoryImp(api)
        }

        val viewModel: HomeViewModel =
            viewModel(factory = HomeViewModelFactory(repository))

        val state by viewModel.uiState.collectAsState()

        AllProductsScreen(
            products = (state as? HomeUiState.Success)?.products.orEmpty(),
            onBackClick = { navController.popBackStack() },
            onProductClick = {
                navController.navigate(HomeRoutes.productDetails(it.id))
            }
        )
    }

    composable(HomeRoutes.AllVendors) {

        val repository = remember {
            val api = RetrofitProvider.create(
                hostName = "mad46-and7.myshopify.com",
                accessToken = "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"
            )
            HomeRepositoryImp(api)
        }

        val viewModel: HomeViewModel =
            viewModel(factory = HomeViewModelFactory(repository))

        AllBrandsScreen(
            viewModel = viewModel,
            onBackClick = { navController.popBackStack() }
        )
    }
}