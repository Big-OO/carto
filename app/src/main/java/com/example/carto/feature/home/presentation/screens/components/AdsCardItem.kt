package com.example.carto.feature.home.presentation.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.carto.feature.home.presentation.screens.model.AdUi


@Composable
fun AdsCardItem(
    ad: AdUi,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = adGradientColors(ad.route),
                    )
                )
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        ad.title,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        ad.subtitle,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        contentPadding = PaddingValues(
                            horizontal = 16.dp,
                            vertical = 6.dp
                        )
                    ) {
                        Text(
                            ad.buttonText,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f),
                            CircleShape
                        )
                )
            }
        }
    }
}
@Composable
private fun adGradientColors(route: String?): List<androidx.compose.ui.graphics.Color> {
    val colorScheme = MaterialTheme.colorScheme
    return when (route) {
        "sale" -> listOf(colorScheme.primary, colorScheme.error)
        "new" -> listOf(colorScheme.tertiary, colorScheme.primary)
        else -> listOf(colorScheme.secondary, colorScheme.primary)
    }
}
