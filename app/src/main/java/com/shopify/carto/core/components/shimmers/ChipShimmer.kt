package com.shopify.carto.core.components.shimmers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.shopify.carto.core.utils.shimmerEffect


@Composable
fun ChipShimmer() {
    Box(
        modifier = Modifier
            .width(80.dp)
            .height(36.dp)
            .clip(RoundedCornerShape(50))
            .shimmerEffect()
    )
}