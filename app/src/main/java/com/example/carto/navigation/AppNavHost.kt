package com.example.carto.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.carto.feature.addresses.presentation.view.AddressesScreen
import com.example.carto.feature.addresses.presentation.view.NewAddressScreen
import com.example.carto.feature.favorite.presentation.FavoriteToastViewModel
import com.example.carto.feature.favorite.presentation.SavedScreen
import com.example.carto.feature.favorite.presentation.components.FavoriteAddedSnackbar
import com.example.carto.feature.forgetpassword.presentation.ForgotPasswordScreen
import com.example.carto.feature.home.navigation.homeGraph
import com.example.carto.feature.login.presentation.LoginScreen
import com.example.carto.feature.map.domain.model.MapAddress
import com.example.carto.feature.map.domain.model.MapPoint
import com.example.carto.feature.map.domain.model.SelectedMapAddress
import com.example.carto.feature.map.presentation.view.MapPickerScreen
import com.example.carto.feature.map.utils.MapResultKeys
import com.example.carto.feature.profile.presentation.ProfileEffect
import com.example.carto.feature.profile.presentation.ProfileScreen
import com.example.carto.feature.profile.presentation.ProfileViewModel
import com.example.carto.feature.register.presentation.view.RegisterScreen
import com.example.carto.feature.search.presentation.view.SearchScreen
import com.example.carto.navigation.PlaceholderScreens.CartPlaceholderScreen
import com.example.carto.navigation.components.AppBottomBar
import com.example.carto.navigation.viewmodel.AppSessionViewModel

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    sessionViewModel: AppSessionViewModel = hiltViewModel(),
    favoriteToastViewModel: FavoriteToastViewModel = hiltViewModel(),
) {
    val session by sessionViewModel.session.collectAsStateWithLifecycle()
    val favoriteToast by favoriteToastViewModel.toast.collectAsStateWithLifecycle()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(
        session?.isLoggedIn,
        session?.isGuest,
        currentRoute,
    ) {
        val currentSession = session

        if (
            currentSession?.isLoggedIn == true &&
            !currentSession.isGuest &&
            currentRoute == Screen.Login.route
        ) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.fillMaxSize(),
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
                    },
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
                    },
                )
            }

            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(
                    onBack = {
                        navController.popBackStack()
                    },
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
                            Screen.ProductDetail.createRoute(productId),
                        )
                    },
                )
            }

            composable(Screen.Saved.route) {
                SavedScreen(
                    onProductClick = { productId ->
                        navController.navigate(
                            Screen.ProductDetail.createRoute(productId),
                        )
                    },
                )
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
                                    popUpTo(0) {
                                        inclusive = true
                                    }
                                }
                            }

                            ProfileEffect.NavigateToSettings -> {
                                // Add settings route later if needed.
                                // navController.navigate(Screen.Settings.route)
                            }

                            else -> Unit
                        }
                    }
                }

                ProfileScreen(
                    uiState = uiState,
                    effectFlow = profileViewModel.effect,
                    onEvent = profileViewModel::onEvent,
                )
            }

            composable(Screen.Addresses.route) {
                AddressesScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onAddNewAddressClick = {
                        navController.navigate(Screen.NewAddress.route)
                    },
                )
            }

            composable(Screen.NewAddress.route) { backStackEntry ->
                val savedStateHandle = backStackEntry.savedStateHandle

                val latitude by savedStateHandle
                    .getStateFlow<Double?>(MapResultKeys.LATITUDE, null)
                    .collectAsStateWithLifecycle()

                val longitude by savedStateHandle
                    .getStateFlow<Double?>(MapResultKeys.LONGITUDE, null)
                    .collectAsStateWithLifecycle()

                val addressLine by savedStateHandle
                    .getStateFlow(MapResultKeys.ADDRESS_LINE, "")
                    .collectAsStateWithLifecycle()

                val city by savedStateHandle
                    .getStateFlow(MapResultKeys.CITY, "")
                    .collectAsStateWithLifecycle()

                val province by savedStateHandle
                    .getStateFlow(MapResultKeys.PROVINCE, "")
                    .collectAsStateWithLifecycle()

                val country by savedStateHandle
                    .getStateFlow(MapResultKeys.COUNTRY, "")
                    .collectAsStateWithLifecycle()

                val zip by savedStateHandle
                    .getStateFlow(MapResultKeys.ZIP, "")
                    .collectAsStateWithLifecycle()

                val selectedMapAddress = latitude?.let { lat ->
                    longitude?.let { lng ->
                        SelectedMapAddress(
                            point = MapPoint(
                                latitude = lat,
                                longitude = lng,
                            ),
                            address = MapAddress(
                                addressLine = addressLine,
                                city = city,
                                province = province,
                                country = country,
                                zip = zip,
                            ),
                        )
                    }
                }

                NewAddressScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSelectFromMapClick = {
                        navController.navigate(Screen.MapPicker.route)
                    },
                    selectedMapAddress = selectedMapAddress,
                    onMapAddressConsumed = {
                        savedStateHandle.remove<Double>(MapResultKeys.LATITUDE)
                        savedStateHandle.remove<Double>(MapResultKeys.LONGITUDE)
                        savedStateHandle.remove<String>(MapResultKeys.ADDRESS_LINE)
                        savedStateHandle.remove<String>(MapResultKeys.CITY)
                        savedStateHandle.remove<String>(MapResultKeys.PROVINCE)
                        savedStateHandle.remove<String>(MapResultKeys.COUNTRY)
                        savedStateHandle.remove<String>(MapResultKeys.ZIP)
                    },
                )
            }

            composable(Screen.MapPicker.route) {
                MapPickerScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onLocationSelected = { result ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.apply {
                                set(MapResultKeys.LATITUDE, result.point.latitude)
                                set(MapResultKeys.LONGITUDE, result.point.longitude)
                                set(MapResultKeys.ADDRESS_LINE, result.address.addressLine)
                                set(MapResultKeys.CITY, result.address.city)
                                set(MapResultKeys.PROVINCE, result.address.province)
                                set(MapResultKeys.COUNTRY, result.address.country)
                                set(MapResultKeys.ZIP, result.address.zip)
                            }

                        navController.popBackStack()
                    },
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
                isGuest = session?.isGuest ?: true,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .zIndex(1f),
            )
        }

        FavoriteAddedSnackbar(
            toast = favoriteToast,
            onViewClick = {
                favoriteToastViewModel.dismiss()

                navController.navigate(Screen.Saved.route) {
                    popUpTo(Screen.Home.route) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            onUndoClick = favoriteToastViewModel::undo,
            onDismiss = favoriteToastViewModel::dismiss,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(2f)
                .padding(
                    bottom = if (currentRoute in bottomBarRoutes) {
                        104.dp
                    } else {
                        20.dp
                    },
                ),
        )
    }
}