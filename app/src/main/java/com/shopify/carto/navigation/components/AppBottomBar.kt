package com.shopify.carto.navigation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.shopify.carto.R
import com.shopify.carto.navigation.NavItem
import com.shopify.carto.navigation.Screen
import com.shopify.carto.navigation.authRequiredRoutes


@Composable
fun AppBottomBar(
    navController: NavController,
    onLoginRequired: () -> Unit,
    isGuest: Boolean,
    modifier: Modifier = Modifier,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var showAuthDialog by remember { mutableStateOf(false) }

    val navItems = listOf(
        NavItem(Screen.Home.route, stringResource(id = R.string.homeNavTitle), Icons.Filled.Home),
        NavItem(Screen.Saved.route, stringResource(id = R.string.savedNavTitle), Icons.Outlined.FavoriteBorder),
        NavItem(Screen.AIAssistant.route, "", Icons.Filled.Home), // Placeholder
        NavItem(Screen.Cart.route, stringResource(id = R.string.cartNavTitle), Icons.Outlined.ShoppingCart),
        NavItem(Screen.Account.route, stringResource(id = R.string.accountNavTitle), Icons.Outlined.AccountCircle)
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(68.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            shadowElevation = 4.dp,
            tonalElevation = 0.dp
        ) {
            NavigationBar(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                tonalElevation = 0.dp,
                windowInsets = WindowInsets(0.dp)
            ) {
                navItems.forEachIndexed { index, item ->
                    val selected = currentRoute == item.route

                    if (index == 2) {
                        NavigationBarItem(
                            selected = false,
                            onClick = {},
                            enabled = false,
                            icon = { Spacer(modifier = Modifier.size(22.dp)) },
                            colors = NavigationBarItemDefaults.colors(
                                disabledIconColor = Color.Transparent,
                                disabledTextColor = Color.Transparent,
                                indicatorColor = Color.Transparent
                            )
                        )
                    } else {
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                val needsAuth = item.route in authRequiredRoutes && isGuest

                                when {
                                    needsAuth -> showAuthDialog = true

                                    !selected -> navController.navigate(item.route) {
                                        popUpTo(Screen.Home.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                if (selected) {
                                    Text(
                                        text = item.label,
                                        fontSize = 11.sp,
                                        maxLines = 1
                                    )
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.surface.copy(alpha = 0f)
                            )
                        )
                    }
                }
            }
        }

        // Elevated Glowing Floating AI Sparkle Button in the center
        val isAIOpen = currentRoute == Screen.AIAssistant.route
        val infiniteTransition = rememberInfiniteTransition(label = "ai_float_glow")

        // 1. Float translation up/down
        val floatTranslationY by infiniteTransition.animateFloat(
            initialValue = -5f,
            targetValue = 5f,
            animationSpec = infiniteRepeatable(
                animation = tween(1800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "floatTranslationY"
        )

        // 2. Glow pulsing scale
        val glowScale by infiniteTransition.animateFloat(
            initialValue = 0.90f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glowScale"
        )

        // 3. Glow opacity pulsing
        val glowAlpha by infiniteTransition.animateFloat(
            initialValue = 0.20f,
            targetValue = 0.60f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glowAlpha"
        )

        // Selected scale state
        val buttonScale by animateFloatAsState(
            targetValue = if (isAIOpen) 1.12f else 1.0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "buttonScale"
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-18).dp)
                .graphicsLayer {
                    translationY = floatTranslationY.dp.toPx()
                }
                .size(72.dp),
            contentAlignment = Alignment.Center
        ) {
            // Radial glow behind the button
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = glowScale * buttonScale
                        scaleY = glowScale * buttonScale
                        alpha = glowAlpha
                    }
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                Color.Transparent
                            )
                        )
                    )
            )

            Surface(
                modifier = Modifier
                    .size(52.dp)
                    .graphicsLayer {
                        scaleX = buttonScale
                        scaleY = buttonScale
                    }
                    .clickable {
                        if (!isAIOpen) {
                            navController.navigate(Screen.AIAssistant.route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                shape = CircleShape,
                color = Color.Transparent,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            )
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White,
                                    Color.White.copy(alpha = 0.3f),
                                    Color.White
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Assistant",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    if (showAuthDialog) {
        AuthRequiredDialog(
            onDismiss = { showAuthDialog = false },
            onLoginClick = {
                showAuthDialog = false
                onLoginRequired()
            }
        )
    }
}