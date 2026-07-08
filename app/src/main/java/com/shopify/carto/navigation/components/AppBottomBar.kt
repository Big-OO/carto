package com.shopify.carto.navigation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
        NavItem(
            Screen.Home.route,
            stringResource(id = R.string.homeNavTitle),
            painterResource(R.drawable.ic_home),
            painterResource(R.drawable.ic_home_selected),
        ),
        NavItem(
            Screen.Saved.route,
            stringResource(id = R.string.savedNavTitle),
            painterResource(R.drawable.ic_heart),
            painterResource(R.drawable.ic_heart_selected),
        ),
        NavItem(
            Screen.Cart.route,
            stringResource(id = R.string.cartNavTitle),
            painterResource(R.drawable.ic_cart),
            painterResource(R.drawable.ic_cart_selected),
        ),
        NavItem(
            Screen.Account.route,
            stringResource(id = R.string.accountNavTitle),
            painterResource(R.drawable.ic_profile),
            painterResource(R.drawable.ic_profile_selected),
        ),
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            shadowElevation = 10.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                navItems.forEachIndexed { index, item ->
                    val selected = currentRoute == item.route

                    BottomBarItem(
                        item = item,
                        selected = selected,
                        modifier = Modifier.weight(1f),
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
                    )

                    if (index == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        AiAssistantButton(
            isSelected = currentRoute == Screen.AIAssistant.route,
            onClick = {
                val needsAuth = Screen.AIAssistant.route in authRequiredRoutes && isGuest

                when {
                    needsAuth -> showAuthDialog = true

                    currentRoute != Screen.AIAssistant.route -> {
                        navController.navigate(Screen.AIAssistant.route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-18).dp),
        )
    }

    if (showAuthDialog) {
        AuthRequiredDialog(
            onDismiss = { showAuthDialog = false },
            onLoginClick = {
                showAuthDialog = false
                onLoginRequired()
            },
        )
    }
}

@Composable
private fun AiAssistantButton(
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ai_float_glow")

    val floatTranslationY by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "floatTranslationY",
    )

    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glowScale",
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.20f,
        targetValue = 0.60f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glowAlpha",
    )

    val buttonScale by animateFloatAsState(
        targetValue = if (isSelected) 1.12f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "buttonScale",
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                translationY = floatTranslationY.dp.toPx()
            }
            .size(72.dp),
        contentAlignment = Alignment.Center,
    ) {
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
                            Color.Transparent,
                        ),
                    ),
                ),
        )

        Surface(
            modifier = Modifier
                .size(52.dp)
                .graphicsLayer {
                    scaleX = buttonScale
                    scaleY = buttonScale
                }
                .clickable(onClick = onClick),
            shape = CircleShape,
            color = Color.Transparent,
            shadowElevation = 8.dp,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.tertiary,
                            ),
                        ),
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White,
                                Color.White.copy(alpha = 0.3f),
                                Color.White,
                            ),
                        ),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI Assistant",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

@Composable
private fun BottomBarItem(
    item: NavItem,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val selectedColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(24.dp))
            .padding(vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .height(34.dp)
                .width(48.dp)
                .clip(RoundedCornerShape(18.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                )
                .background(Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = if (selected) item.selectedIcon else item.icon,
                contentDescription = item.label,
                tint = if (selected) selectedColor else unselectedColor,
                modifier = Modifier.size(22.dp),
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        if (selected) {
            Text(
                text = item.label,
                color = selectedColor,
                fontSize = 10.sp,
                lineHeight = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}