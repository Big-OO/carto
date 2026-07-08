package com.shopify.carto.feature.ai_integration.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shopify.carto.R

@Composable
fun VoiceRecordingBar(
    rmsHistory: List<Float>,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "recPulse")
    val recAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "recAlpha"
    )

    Surface(
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Pulsing red record dot
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color(0xFFE53935).copy(alpha = recAlpha), CircleShape)
            )
            Text(
                text = stringResource(id = R.string.ai_recording),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Medium
            )
            // Live waveform bars from rmsHistory
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val displayBars = if (rmsHistory.isEmpty()) List(20) { 0.2f } else rmsHistory
                displayBars.takeLast(24).forEach { rms ->
                    val normalised = (rms / 12f).coerceIn(0.05f, 1f)
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height((normalised * 28).dp + 4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.75f))
                    )
                }
            }
            // Cancel button
            TextButton(
                onClick = onCancelClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.commonCancel),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
