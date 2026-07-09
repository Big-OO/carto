package com.shopify.carto.feature.splash.presentation.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.shopify.carto.feature.splash.presentation.state.SplashDestination
import com.shopify.carto.feature.splash.presentation.state.SplashEffect
import com.shopify.carto.feature.splash.presentation.viewmodel.SplashInteractionListener
import com.shopify.carto.feature.splash.presentation.viewmodel.SplashViewModel
import com.shopify.carto.presentation.components.CartoLogo
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


@Composable
fun SplashScreen(
    onNavigateToOnBoarding: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is SplashEffect.Navigate -> {
                        when (effect.destination) {
                            SplashDestination.OnBoarding -> onNavigateToOnBoarding()
                            SplashDestination.Login -> onNavigateToLogin()
                            SplashDestination.Home -> onNavigateToHome()
                        }
                    }
                }
            }
        }
    }

    SplashContent(
        interactionListener = viewModel
    )
}

@Composable
fun SplashContent(
    interactionListener: SplashInteractionListener,
    modifier: Modifier = Modifier,
) {
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.78f) }
    val offsetY = remember { Animatable(40f) }

    LaunchedEffect(Unit) {
        val job = coroutineScope {
            launch {
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 700,
                        easing = FastOutSlowInEasing,
                    ),
                )
            }

            launch {
                scale.animateTo(
                    targetValue = 1.08f,
                    animationSpec = tween(
                        durationMillis = 700,
                        easing = FastOutSlowInEasing,
                    ),
                )

                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow,
                    ),
                )
            }

            launch {
                offsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = 700,
                        easing = FastOutSlowInEasing,
                    ),
                )
            }
        }

        job.join()
        interactionListener.onSplashAnimationFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        CartoLogo(
            modifier = Modifier
                .size(150.dp)
                .graphicsLayer {
                    this.alpha = alpha.value
                    scaleX = scale.value
                    scaleY = scale.value
                    translationY = offsetY.value
                },
        )
    }
}