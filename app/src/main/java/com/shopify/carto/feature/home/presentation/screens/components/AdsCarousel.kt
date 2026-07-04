package com.shopify.carto.feature.home.presentation.screens.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.shopify.carto.feature.home.presentation.screens.model.AdUi
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun AdsCarousel(
    ads: List<AdUi>,
    onAdClick: (AdUi) -> Unit
) {

    val pagerState = rememberPagerState(pageCount = { ads.size })

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000.milliseconds)
            val next = (pagerState.currentPage + 1) % ads.size
            pagerState.animateScrollToPage(next)
        }
    }

    Column {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
        ) { page ->

            val ad = ads[page]

            AdsCardItem(
                ad = ad,
                onClick = { onAdClick(ad) }
            )
        }

        Spacer(Modifier.height(10.dp))

        // Indicator Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(ads.size) { index ->
                val selected = pagerState.currentPage == index
                val dotWidth by animateDpAsState(
                    targetValue = if (selected) 18.dp else 6.dp,
                    label = "dot_width"
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 3.dp)
                        .width(dotWidth)
                        .height(6.dp)
                        .background(
                            color = if (selected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}