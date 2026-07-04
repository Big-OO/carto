package com.example.carto.feature.splash.presentation.view

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.carto.R
import com.example.carto.feature.splash.presentation.state.SplashEffect
import com.example.carto.feature.splash.presentation.viewmodel.SplashInteractionListener
import com.example.carto.feature.splash.presentation.viewmodel.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToOnBoarding: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SplashEffect.NavigateToOnBoarding -> onNavigateToOnBoarding()
                SplashEffect.NavigateToLogin -> onNavigateToLogin()
                SplashEffect.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    SplashContent(
        interactionListener = viewModel,
        modifier = modifier,
    )
}

@Composable
private fun SplashContent(
    interactionListener: SplashInteractionListener,
    modifier: Modifier = Modifier,
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(120)
        isVisible = true
        delay(1_700)
        interactionListener.onSplashAnimationFinished()
    }

    val logoAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 700,
            easing = FastOutSlowInEasing,
        ),
        label = "splash_logo_alpha",
    )

    val logoScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.72f,
        animationSpec = tween(
            durationMillis = 900,
            easing = FastOutSlowInEasing,
        ),
        label = "splash_logo_scale",
    )

    val contentOffset by animateFloatAsState(
        targetValue = if (isVisible) 0f else 42f,
        animationSpec = tween(
            durationMillis = 850,
            easing = FastOutSlowInEasing,
        ),
        label = "splash_content_offset",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.background(MaterialTheme.colorScheme.background).graphicsLayer {
                alpha = logoAlpha
                scaleX = logoScale
                scaleY = logoScale
                translationY = contentOffset
            },
        ) {
            Image(
                painter = painterResource(R.drawable.logo_carto),
                contentDescription = stringResource(R.string.splash_logo_content_description),
                modifier = Modifier.size(width = 220.dp, height = 140.dp),
            )
        }
    }
}
