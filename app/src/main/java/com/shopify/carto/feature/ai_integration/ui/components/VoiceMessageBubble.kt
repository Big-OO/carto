package com.shopify.carto.feature.ai_integration.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VoiceMessageBubble(
    modifier: Modifier = Modifier
) {
    // Static decorative waveform (real rmsHistory not stored per-message for simplicity)
    val barHeights = remember {
        listOf(0.3f, 0.6f, 0.9f, 0.5f, 0.8f, 0.4f, 1.0f, 0.6f, 0.3f, 0.7f,
               0.5f, 0.9f, 0.4f, 0.8f, 0.6f, 0.3f, 0.7f, 1.0f, 0.5f, 0.4f)
    }
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voice message",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(16.dp)
            )
            // Waveform bars
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                barHeights.forEach { h ->
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height((h * 20).dp + 4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.onPrimary)
                    )
                }
            }
        }
    }
}
