package com.shopify.carto.feature.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shopify.carto.ui.theme.CartoTheme

@Composable
fun ProfileHeader(
    name: String,
    id: String,
    modifier: Modifier = Modifier
) {
    val primaryColor = CartoTheme.colors.primary
    val secondaryColor = CartoTheme.colors.secondary
    val gradientBrush = Brush.linearGradient(
        colors = listOf(primaryColor, secondaryColor)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .border(3.dp, primaryColor.copy(alpha = 0.2f), CircleShape)
                .padding(6.dp)
                .background(gradientBrush, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name
                    .split(" ")
                    .filter { it.isNotEmpty() }
                    .take(2)
                    .joinToString("") { it.firstOrNull()?.toString().orEmpty() }
                    .uppercase(),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = CartoTheme.colors.onBackground
        )

        Spacer(Modifier.height(6.dp))

        Surface(
            shape = CircleShape,
            color = CartoTheme.colors.primaryContainer.copy(alpha = 0.3f)
        ) {
            Text(
                text = "Customer ID: $id",
                color = CartoTheme.colors.onPrimaryContainer,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
            )
        }
    }
}