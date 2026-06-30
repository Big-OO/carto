package com.example.carto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
//import com.example.carto.navigation.AppNavHost
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.carto.feature.home.presentation.HomeScreen
import com.example.carto.feature.login.presentation.loginGraph
import com.example.carto.navigation.AppNavHost
import com.example.carto.ui.theme.CartoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CartoTheme(darkTheme = false) {
//                val navController = rememberNavController()
//                NavHost(
//                    navController = navController,
//                    startDestination = "login_graph",
//                    modifier = Modifier.fillMaxSize()
//                ) {
//                    loginGraph(
//                        navController = navController,
//                        onNavigateToHome = {
//                            navController.navigate("home") {
//                                popUpTo("login_graph") { inclusive = true }
//                            }
//                        }
//                    )
//                    composable("home") {
//                        HomeScreen(
//                            onBackToLogin = {
//                                navController.navigate("login_graph") {
//                                    popUpTo("home") { inclusive = true }
//                                }
//                            }
//                        )
//                    }
//                }
                AppNavHost()
            }
        }
    }
}


