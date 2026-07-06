package com.shopify.carto.navigation

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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.carto.product_reviews.presentation.screen.ProductReviewsScreen
import com.shopify.carto.feature.addresses.presentation.view.AddressesScreen
import com.shopify.carto.feature.addresses.presentation.view.NewAddressScreen
import com.shopify.carto.feature.favorite.presentation.FavoriteToastViewModel
import com.shopify.carto.feature.favorite.presentation.SavedScreen
import com.shopify.carto.feature.favorite.presentation.components.FavoriteAddedSnackbar
import com.shopify.carto.feature.forgetpassword.presentation.ForgotPasswordScreen
import com.shopify.carto.feature.home.navigation.homeGraph
import com.shopify.carto.feature.login.presentation.LoginScreen
import com.shopify.carto.feature.orderdetails.presentation.view.OrderDetailsScreen
import com.shopify.carto.feature.orderhistory.presentation.view.OrderHistoryScreen
import com.shopify.carto.feature.map.domain.model.MapAddress
import com.shopify.carto.feature.map.domain.model.MapPoint
import com.shopify.carto.feature.map.domain.model.SelectedMapAddress
import com.shopify.carto.feature.map.presentation.view.MapPickerScreen
import com.shopify.carto.feature.map.utils.MapResultKeys
import com.shopify.carto.feature.payment.presentation.view.CheckoutScreen
import com.shopify.carto.feature.payment.presentation.view.PaymentResultScreen
import com.shopify.carto.feature.payment.presentation.viewmodel.CheckoutViewModel
import com.shopify.carto.feature.product_details.presentation.ProductDetailsScreen
import com.shopify.carto.feature.profile.presentation.ProfileEffect
import com.shopify.carto.feature.profile.presentation.ProfileScreen
import com.shopify.carto.feature.profile.presentation.ProfileViewModel
import com.shopify.carto.feature.register.presentation.view.RegisterScreen
import com.shopify.carto.feature.search.presentation.view.SearchScreen
import com.shopify.carto.feature.settings.presentation.SettingsScreen
import com.shopify.carto.feature.shopping_cart.presentation.CartScreen
import com.shopify.carto.feature.splash.presentation.view.SplashScreen
import com.shopify.carto.navigation.components.AppBottomBar
import com.shopify.carto.navigation.viewmodel.AppSessionViewModel
import com.shopify.carto.on_boarding.OnBoardingScreen

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
            startDestination = Screen.Splash.route,
            modifier = Modifier.fillMaxSize(),
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToOnBoarding = {
                        navController.navigate(Screen.onBoarding.route) {
                            popUpTo(Screen.Splash.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                )
            }

            composable(Screen.onBoarding.route) {
                OnBoardingScreen(
                    onFinishOnboarding = {
                        sessionViewModel.completeOnBoarding {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.onBoarding.route) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    },
                    onLoginClick = {
                        sessionViewModel.completeOnBoarding {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.onBoarding.route) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    },
                )
            }

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

            composable(
                route = Screen.ProductDetail.route,
                arguments = listOf(
                    navArgument("productId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val productId =
                    backStackEntry.arguments?.getLong("productId")?.toString() ?: return@composable

                ProductDetailsScreen(
                    productId = productId,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onNavigateToCart = {
                        navController.navigate(Screen.Cart.route) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToReviews = {
                        navController.navigate(
                            Screen.ProductReviews.route.replace(
                                "{productId}",
                                productId
                            )
                        ) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Screen.Cart.route) {
                CartScreen(

                    onNavigateToCheckout = { checkoutUrl ->

                    }
                )
            }

            composable(Screen.ProductReviews.route) { backStackEntry ->
                val productId =
                    backStackEntry.arguments?.getString("productId") ?: return@composable

                ProductReviewsScreen(
                    productId = productId,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Checkout.route) { backStackEntry ->
                val checkoutViewModel: CheckoutViewModel = hiltViewModel(backStackEntry)

                CheckoutScreen(
                    viewModel = checkoutViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onPaymentSuccess = { transactionId, orderId ->
                        navController.navigate(
                            Screen.PaymentResult.createRoute(
                                success = true,
                                transactionId = orderId.ifBlank { transactionId },
                            )
                        ) {
                            popUpTo(Screen.Checkout.route) {
                                inclusive = true
                            }
                        }
                    },
                    onPaymentFailed = { message ->
                        navController.navigate(
                            Screen.PaymentResult.createRoute(
                                success = false,
                                transactionId = message,
                            )
                        ) {
                            popUpTo(Screen.Checkout.route) {
                                inclusive = true
                            }
                        }
                    },
                )
            }

            composable(
                route = Screen.PaymentResult.route,
                arguments = listOf(
                    navArgument("success") { type = NavType.BoolType },
                    navArgument("transactionId") { type = NavType.StringType },
                ),
            ) { backStackEntry ->
                val isSuccess = backStackEntry.arguments?.getBoolean("success") ?: false
                val transactionId = backStackEntry.arguments?.getString("transactionId").orEmpty()

                PaymentResultScreen(
                    isSuccess = isSuccess,
                    transactionId = transactionId,
                    onContinueShopping = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onRetryPayment = {
                        navController.navigate(Screen.Checkout.route) {
                            popUpTo(Screen.PaymentResult.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                )
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
                                navController.navigate(Screen.Settings.route)
                            }

                            ProfileEffect.NavigateToOrders -> {
                                navController.navigate(Screen.OrderHistory.route)
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


            composable(Screen.OrderHistory.route) {
                OrderHistoryScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onOrderDetailsClick = { orderId ->
                        navController.navigate(Screen.OrderDetails.createRoute(orderId))
                    },
                )
            }

            composable(
                route = Screen.OrderDetails.route,
                arguments = listOf(
                    navArgument("orderId") { type = NavType.StringType },
                ),
            ) {
                OrderDetailsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onNavigateToAddressesClick = {
                        navController.navigate(Screen.Addresses.route)
                    },
                    onNavigateToPaymentMethodsClick = {
                        navController.navigate(Screen.Checkout.route)
                    }
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
                        popUpTo(0) {
                            inclusive = true
                        }
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
