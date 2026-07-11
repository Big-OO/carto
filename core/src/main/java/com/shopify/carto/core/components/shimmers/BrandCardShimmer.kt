package com.shopify.carto.core.components.shimmers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.shopify.carto.core.utils.shimmerEffect


@Composable
fun BrandCardShimmer(compact: Boolean = true) {
    Column(
        modifier = Modifier.padding(if (compact) 8.dp else 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(if (compact) 64.dp else 96.dp)
                .clip(CircleShape)
                .shimmerEffect()
        )

        Spacer(Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .width(if (compact) 60.dp else 80.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect()
        )
    }
}
