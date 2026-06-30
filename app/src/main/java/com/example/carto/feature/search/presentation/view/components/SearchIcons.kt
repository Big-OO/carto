package com.example.carto.feature.search.presentation.view.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun CloseCircleIcon(
    modifier: Modifier = Modifier,
    color: Color? = null,
) {
    val iconColor = color ?: MaterialTheme.colorScheme.onSurfaceVariant

    Canvas(modifier = modifier.size(28.dp)) {
        drawCircle(
            color = iconColor,
            radius = size.minDimension * 0.38f,
            center = Offset(size.width / 2f, size.height / 2f),
            style = Stroke(width = 4f, cap = StrokeCap.Round),
        )
        drawLine(
            color = iconColor,
            start = Offset(size.width * 0.38f, size.height * 0.38f),
            end = Offset(size.width * 0.62f, size.height * 0.62f),
            strokeWidth = 4f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = iconColor,
            start = Offset(size.width * 0.62f, size.height * 0.38f),
            end = Offset(size.width * 0.38f, size.height * 0.62f),
            strokeWidth = 4f,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun OpenArrowIcon(
    modifier: Modifier = Modifier,
    color: Color? = null,
) {
    val iconColor = color ?: MaterialTheme.colorScheme.onSurface

    Canvas(modifier = modifier.size(32.dp)) {
        drawLine(
            color = iconColor,
            start = Offset(size.width * 0.22f, size.height * 0.78f),
            end = Offset(size.width * 0.78f, size.height * 0.22f),
            strokeWidth = 4.5f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = iconColor,
            start = Offset(size.width * 0.78f, size.height * 0.22f),
            end = Offset(size.width * 0.78f, size.height * 0.56f),
            strokeWidth = 4.5f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = iconColor,
            start = Offset(size.width * 0.78f, size.height * 0.22f),
            end = Offset(size.width * 0.44f, size.height * 0.22f),
            strokeWidth = 4.5f,
            cap = StrokeCap.Round,
        )
    }
}
