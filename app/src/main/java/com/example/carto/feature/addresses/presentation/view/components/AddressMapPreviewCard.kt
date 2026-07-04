package com.example.carto.feature.addresses.presentation.view.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.carto.R

@Composable
fun AddressMapPreviewCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 2.dp,
    ) {
        Box {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFEAF4FF),
                            Color(0xFFF8FAFC),
                            Color(0xFFE8F7EF),
                        ),
                    )
                )

                val globeRadius = size.minDimension * 0.33f
                val center = Offset(size.width * 0.78f, size.height * 0.48f)

                drawCircle(
                    color = Color(0xFFDBEAFE),
                    radius = globeRadius,
                    center = center,
                )
                drawCircle(
                    color = Color(0xFF93C5FD),
                    radius = globeRadius,
                    center = center,
                    style = Stroke(width = 4.dp.toPx()),
                )
                drawArc(
                    color = Color(0xFF60A5FA),
                    startAngle = -80f,
                    sweepAngle = 160f,
                    useCenter = false,
                    topLeft = Offset(center.x - globeRadius * 0.55f, center.y - globeRadius),
                    size = Size(globeRadius * 1.1f, globeRadius * 2f),
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
                )
                drawArc(
                    color = Color(0xFF60A5FA),
                    startAngle = 100f,
                    sweepAngle = 160f,
                    useCenter = false,
                    topLeft = Offset(center.x - globeRadius * 0.55f, center.y - globeRadius),
                    size = Size(globeRadius * 1.1f, globeRadius * 2f),
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
                )
                drawLine(
                    color = Color(0xFF60A5FA),
                    start = Offset(center.x - globeRadius, center.y),
                    end = Offset(center.x + globeRadius, center.y),
                    strokeWidth = 2.dp.toPx(),
                )
                drawLine(
                    color = Color(0xFF60A5FA),
                    start = Offset(center.x, center.y - globeRadius),
                    end = Offset(center.x, center.y + globeRadius),
                    strokeWidth = 2.dp.toPx(),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = stringResource(R.string.addresses_select_location_from_map),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = stringResource(R.string.addresses_select_location_from_map_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
