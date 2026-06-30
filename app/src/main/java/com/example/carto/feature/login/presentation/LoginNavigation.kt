package com.example.carto.feature.login.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.carto.feature.forgetpassword.presentation.ForgotPasswordScreen
import com.example.carto.feature.register.presentation.view.RegisterScreen

fun NavGraphBuilder.loginGraph(
    navController: NavController,
    onNavigateToHome: () -> Unit
) {
    navigation(startDestination = "login", route = "login_graph") {
        composable("login") {
            LoginScreen(
                modifier = Modifier.fillMaxSize(),
                onNavigateToHome = onNavigateToHome,
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") }
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable("forgot_password") {
            ForgotPasswordScreen(onBack = { navController.popBackStack() })
        }
    }
}
