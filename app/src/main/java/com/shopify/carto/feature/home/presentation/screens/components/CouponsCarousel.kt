package com.shopify.carto.feature.home.presentation.screens.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.shopify.carto.feature.home.domain.model.Coupon
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CouponsCarousel(
    coupons: List<Coupon>,
    onCopyCodeClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (coupons.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { coupons.size })

    LaunchedEffect(coupons.size) {
        if (coupons.size <= 1) return@LaunchedEffect

        while (true) {
            delay(4000.milliseconds)
            val next = (pagerState.currentPage + 1) % coupons.size
            pagerState.animateScrollToPage(next)
        }
    }

    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentPadding = PaddingValues(horizontal = 0.dp),
            pageSpacing = 0.dp
        ) { page ->
            CouponCardItem(
                coupon = coupons[page],
                couponNumber = page,
                onCopyCodeClick = onCopyCodeClick,
                modifier = Modifier
                    .graphicsLayer {
                        // Calculate how far the page is from the current scroll position
                        val pageOffset = abs((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                        
                        // Simple alpha fade to prevent shadow distortion
                        val alpha = 0.6f + (1f - 0.6f) * (1f - pageOffset.coerceIn(0f, 1f))
                        this.alpha = alpha
                    }
            )
        }

        if (coupons.size > 1) {
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(coupons.size) { index ->
                    val selected = pagerState.currentPage == index
                    val dotWidth by animateDpAsState(
                        targetValue = if (selected) 18.dp else 6.dp,
                        label = "coupon_dot_width",
                    )

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 3.dp)
                            .width(dotWidth)
                            .height(6.dp)
                            .background(
                                color = if (selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                },
                                shape = CircleShape,
                            ),
                    )
                }
            }
        }
    }
}