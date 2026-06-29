package com.example.carto.home.presentation.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.carto.home.presentation.screens.model.AdUi
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            repeat(ads.size) { index ->

                val selected = pagerState.currentPage == index

                Box(
                    modifier = Modifier
                        .padding(3.dp)
                        .size(if (selected) 9.dp else 6.dp)
                        .background(
                            color = if (selected)
                                MaterialTheme.colorScheme.primary
                            else
                                Color.Gray.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}