package com.shopify.carto.feature.search.presentation.view.components

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

@Composable
fun HistoryClockIcon(
    modifier: Modifier = Modifier,
    color: Color? = null,
) {
    val iconColor = color ?: MaterialTheme.colorScheme.onSurfaceVariant

    Canvas(modifier = modifier.size(18.dp)) {
        val strokeWidth = 2.4f
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension * 0.36f

        drawCircle(
            color = iconColor,
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
        )
        drawLine(
            color = iconColor,
            start = center,
            end = Offset(size.width * 0.5f, size.height * 0.28f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = iconColor,
            start = center,
            end = Offset(size.width * 0.66f, size.height * 0.52f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = iconColor,
            start = Offset(size.width * 0.16f, size.height * 0.28f),
            end = Offset(size.width * 0.08f, size.height * 0.48f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = iconColor,
            start = Offset(size.width * 0.16f, size.height * 0.28f),
            end = Offset(size.width * 0.36f, size.height * 0.26f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun ChipCloseIcon(
    modifier: Modifier = Modifier,
    color: Color? = null,
) {
    val iconColor = color ?: MaterialTheme.colorScheme.onSurfaceVariant

    Canvas(modifier = modifier.size(14.dp)) {
        drawLine(
            color = iconColor,
            start = Offset(size.width * 0.25f, size.height * 0.25f),
            end = Offset(size.width * 0.75f, size.height * 0.75f),
            strokeWidth = 2.4f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = iconColor,
            start = Offset(size.width * 0.75f, size.height * 0.25f),
            end = Offset(size.width * 0.25f, size.height * 0.75f),
            strokeWidth = 2.4f,
            cap = StrokeCap.Round,
        )
    }
}
