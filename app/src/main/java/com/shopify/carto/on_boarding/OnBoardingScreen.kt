package com.shopify.carto.on_boarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shopify.carto.R
import com.shopify.carto.ui.theme.CartoTheme
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun OnBoardingScreen(
    onFinishOnboarding: () -> Unit,
    onLoginClick: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier
        .fillMaxSize()
        .background(CartoTheme.colors.background)) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->

            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val scaleAnim = 1f - (pageOffset.absoluteValue * 0.15f).coerceIn(0f, 1f)
            val alphaAnim = 1f - (pageOffset.absoluteValue * 0.5f).coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scaleAnim
                        scaleY = scaleAnim
                        alpha = alphaAnim
                    }
            ) {
                when (page) {
                    0 -> PageOneContent()
                    1 -> PageTwoContent()
                    2 -> PageThreeContent()
                }
            }
        }

        AnimatedVisibility(
            visible = pagerState.currentPage < 2,
            enter = fadeIn(animationSpec = tween(400)),
            exit = fadeOut(animationSpec = tween(400)),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 24.dp)
        ) {
            TextButton(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(2)
                    }
                },
                colors = ButtonDefaults.textButtonColors(contentColor = CartoTheme.colors.onBackground)
            ) {
                Text(
                    text = "Skip",
                    style = CartoTheme.typography.labelLarge,
                    color = CartoTheme.colors.onBackground
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, CartoTheme.colors.background, CartoTheme.colors.background),
                        startY = 0f
                    )
                )
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp, top = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            PagerIndicator(
                pageCount = 3,
                currentPage = pagerState.currentPage,
                onPageSelected = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedContent(
                targetState = pagerState.currentPage == 2,
                transitionSpec = {
                    fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
                },
                label = "button_animation"
            ) { isLastPage ->
                Button(
                    onClick = {
                        if (isLastPage) {
                            onFinishOnboarding()
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CartoTheme.colors.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isLastPage) "Get Started" else "Next",
                            color = CartoTheme.colors.onPrimary,
                            style = CartoTheme.typography.labelLarge,
                            letterSpacing = if (isLastPage) 0.sp else 1.sp
                        )
                        if (isLastPage) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = CartoTheme.colors.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            val loginAlpha by animateFloatAsState(
                targetValue = if (pagerState.currentPage == 2) 1f else 0f,
                animationSpec = tween(500),
                label = "login_alpha"
            )

            Text(
                text = "Log In",
                style = CartoTheme.typography.labelLarge,
                color = CartoTheme.colors.onBackground,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .graphicsLayer { alpha = loginAlpha }
                    .clickable(
                        enabled = pagerState.currentPage == 2,
                        onClick = onLoginClick
                    )
                    .padding(8.dp)
            )
        }
    }
}

@Composable
private fun PageOneContent() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.onboarding1),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .align(Alignment.BottomCenter)
        )

        Text(
            text = "Define\nyourself in\nyour unique\nway.",
            style = CartoTheme.typography.headlineLarge,
            color = CartoTheme.colors.onBackground,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 96.dp)
                .align(Alignment.TopStart)
        )
    }
}

@Composable
private fun PageTwoContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(96.dp))

        Image(
            painter = painterResource(id = R.drawable.onboarding2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(CartoTheme.colors.surfaceVariant)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Elevate Your Everyday Style.",
            style = CartoTheme.typography.titleMedium,
            color = CartoTheme.colors.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Discover curated collections tailored to your\npersonal aesthetic.",
            style = CartoTheme.typography.bodyMedium,
            color = CartoTheme.colors.secondary
        )
    }
}

@Composable
private fun PageThreeContent() {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.55f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.onboarding3),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.45f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Ready to shop\nthe\nlatest trends?",
                style = CartoTheme.typography.headlineMedium,
                color = CartoTheme.colors.onBackground,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PagerIndicator(
    pageCount: Int,
    currentPage: Int,
    onPageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onPageSelected(index) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) CartoTheme.colors.primary else CartoTheme.colors.outline)
                )
            }
        }
    }
}