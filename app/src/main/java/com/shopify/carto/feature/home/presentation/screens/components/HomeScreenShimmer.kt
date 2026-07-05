package com.shopify.carto.feature.home.presentation.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.shopify.carto.core.components.shimmers.BrandCardShimmer
import com.shopify.carto.core.components.shimmers.CategoryCardShimmer
import com.shopify.carto.core.components.shimmers.ProductCardShimmer
import com.shopify.carto.core.utils.shimmerEffect


@Composable
fun HomeScreenShimmer(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item { AdsShimmer() }
        item { ShimmerSectionRail(itemCount = 4) { CategoryCardShimmer() } }
        item {
            ShimmerSectionRail(itemCount = 3) {
                ProductCardShimmer()
            }
        }
        item { ShimmerSectionRail(itemCount = 4) { BrandCardShimmer() } }
        item { Spacer(Modifier.height(96.dp)) }
    }
}

@Composable
private fun AdsShimmer() {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .clip(RoundedCornerShape(20.dp))
                .shimmerEffect()
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 3.dp)
                        .width(if (index == 0) 18.dp else 6.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                        .shimmerEffect()
                )
            }
        }
    }
}


@Composable
private fun ShimmerSectionRail(
    itemCount: Int,
    content: @Composable () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
            Box(
                modifier = Modifier
                    .width(50.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
        }

        Spacer(Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(itemCount) {
                Box(modifier = Modifier.width(150.dp)) {
                    content()
                }
            }
        }
    }
}
