package com.shopify.carto.feature.ai_integration.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ThinkingBubble(
    statusMessage: String,
    modifier: Modifier = Modifier
) {
    val t = rememberInfiniteTransition(label = "think")
    val d1 by t.animateFloat(0.2f, 1f, infiniteRepeatable(tween(500, delayMillis = 0), RepeatMode.Reverse), "d1")
    val d2 by t.animateFloat(0.2f, 1f, infiniteRepeatable(tween(500, delayMillis = 160), RepeatMode.Reverse), "d2")
    val d3 by t.animateFloat(0.2f, 1f, infiniteRepeatable(tween(500, delayMillis = 320), RepeatMode.Reverse), "d3")

    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        // AI avatar
        Surface(
            modifier = Modifier.padding(end = 6.dp, bottom = 2.dp).size(26.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(5.dp)
            )
        }

        // Bubble — no border, no shadow
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(
                    text = statusMessage,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(d1, d2, d3).forEach { alpha ->
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = alpha),
                                    CircleShape
                                )
                        )
                    }
                }
            }
        }
    }
}
