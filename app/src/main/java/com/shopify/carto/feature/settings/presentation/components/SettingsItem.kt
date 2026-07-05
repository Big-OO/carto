package com.shopify.carto.feature.settings.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.shopify.carto.ui.theme.CartoTheme

@Composable
fun SettingsItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    iconTint: Color = CartoTheme.colors.primary,
    textColor: Color = CartoTheme.colors.primary,
    subtitleColor: Color = CartoTheme.colors.secondary,
    actionIcon: ImageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
    actionIconTint: Color = CartoTheme.colors.secondary
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = CartoTheme.typography.titleSmall,
                color = textColor
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = CartoTheme.typography.bodySmall,
                    color = subtitleColor
                )
            }
        }

        Icon(
            imageVector = actionIcon,
            contentDescription = "Navigate",
            tint = actionIconTint,
            modifier = Modifier.size(20.dp)
        )
    }
}
