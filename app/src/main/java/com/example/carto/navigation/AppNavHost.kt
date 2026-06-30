package com.example.carto.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.carto.home.navigation.homeGraph
import com.example.carto.navigation.PlaceholderScreens.AccountPlaceholderScreen
import com.example.carto.navigation.PlaceholderScreens.CartPlaceholderScreen
import com.example.carto.navigation.PlaceholderScreens.HomePlaceholderScreen
import com.example.carto.navigation.PlaceholderScreens.SavedPlaceholderScreen
import com.example.carto.navigation.PlaceholderScreens.SearchPlaceholderScreen
import com.example.carto.navigation.components.AppBottomBar


@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                AppBottomBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .padding(innerPadding)
        ) {
           homeGraph(navController)
//            composable(Screen.Home.route) {
//                HomePlaceholderScreen()
//            }

            composable(Screen.Search.route) {
                SearchPlaceholderScreen()
            }

            composable(Screen.Saved.route) {
                SavedPlaceholderScreen()
            }

            composable(Screen.Cart.route) {
                CartPlaceholderScreen()
            }

            composable(Screen.Account.route) {
                AccountPlaceholderScreen()
            }
        }
    }
}