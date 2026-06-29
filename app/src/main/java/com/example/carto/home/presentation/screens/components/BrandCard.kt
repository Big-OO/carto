package com.example.carto.home.presentation.screens.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.carto.home.domain.mappers.VendorUi


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

private val avatarPalette = listOf(
    Color(0xFFEF5350), Color(0xFF42A5F5), Color(0xFF66BB6A),
    Color(0xFFFFA726), Color(0xFFAB47BC), Color(0xFF26A69A),
    Color(0xFFEC407A), Color(0xFF7E57C2)
)

private fun colorForVendor(name: String): Color =
    avatarPalette[name.hashCode().mod(avatarPalette.size)]

@Composable
fun BrandCard(
    vendor: VendorUi,
    modifier: Modifier = Modifier,
    compact: Boolean = true
) {
    val accentColor = colorForVendor(vendor.name)

    Card(
        modifier = modifier.width(if (compact) 100.dp else 160.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (compact) 8.dp else 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(if (compact) 48.dp else 64.dp)
                    .background(accentColor.copy(alpha = 0.15f), shape = RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = vendor.name.take(1).uppercase(),
                    style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = vendor.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )

            Text(
                text = "${vendor.productCount} items",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!compact) {
                Spacer(Modifier.height(4.dp))
                AssistChip(
                    onClick = {},
                    label = { Text(vendor.mainCategory, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }
    }
}
