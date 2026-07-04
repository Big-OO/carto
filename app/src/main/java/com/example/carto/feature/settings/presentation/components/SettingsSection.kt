package com.example.carto.feature.settings.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.carto.ui.theme.CartoTheme


@Composable
fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    titleColor: Color = CartoTheme.colors.primary,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = CartoTheme.typography.titleSmall,
            color = titleColor,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )
        content()
    }
}
