package com.example.carto.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.carto.feature.forgetpassword.presentation.ForgotPasswordScreen
import com.example.carto.feature.home.navigation.homeGraph
import com.example.carto.feature.login.presentation.LoginScreen
import com.example.carto.feature.profile.presentation.ProfileEffect
import com.example.carto.feature.profile.presentation.ProfileScreen
import com.example.carto.feature.profile.presentation.ProfileViewModel
import com.example.carto.feature.register.presentation.view.RegisterScreen
import com.example.carto.feature.search.presentation.view.SearchScreen
import com.example.carto.navigation.PlaceholderScreens.CartPlaceholderScreen
import com.example.carto.navigation.PlaceholderScreens.SavedPlaceholderScreen
import com.example.carto.navigation.components.AppBottomBar
import com.example.carto.navigation.viewmodel.AppSessionViewModel


@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    sessionViewModel: AppSessionViewModel = hiltViewModel(),
) {
    val session by sessionViewModel.session.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(session.isLoggedIn, session.isGuest, currentRoute) {
        if (session.isLoggedIn && !session.isGuest && currentRoute == Screen.Login.route) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    onNavigateToForgotPassword = {
                        navController.navigate(Screen.ForgotPassword.route)
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            homeGraph(navController)

            composable(Screen.Search.route) {
                SearchScreen(
                    onBackClick = {
                        if (!navController.popBackStack()) {
                            navController.navigate(Screen.Home.route) {
                                launchSingleTop = true
                            }
                        }
                    },
                    onProductClick = { productId ->
                        navController.navigate(
                            Screen.ProductDetail.createRoute(productId)
                        )
                    }
                )
            }

            composable(Screen.Saved.route) {
                SavedPlaceholderScreen()
            }

            composable(Screen.Cart.route) {
                CartPlaceholderScreen()
            }

            composable(Screen.Account.route) {
                val profileViewModel: ProfileViewModel = hiltViewModel()
                val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    profileViewModel.effect.collect { effect ->
                        when (effect) {
                            ProfileEffect.NavigateToLogin -> {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                            ProfileEffect.NavigateToSettings -> {
                                // TODO: handle settings navigation
                            }
                            else -> {}
                        }
                    }
                }

                ProfileScreen(
                    uiState = uiState,
                    effectFlow = profileViewModel.effect,
                    onEvent = { event -> profileViewModel.onEvent(event) }
                )
            }
        }

        if (currentRoute in bottomBarRoutes) {
            AppBottomBar(
                navController = navController,
                onLoginRequired = {
                    sessionViewModel.clearSession()
                    navController.navigate(Screen.Login.route) {
                        launchSingleTop = true
                    }
                },
                isGuest = session.isGuest,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .zIndex(1f)
            )
        }
    }
}