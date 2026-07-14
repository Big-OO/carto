package com.shopify.carto.feature.home.presentation.screens.components

import android.util.Log
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


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.BrandingWatermark
import androidx.compose.material.icons.filled.BrandingWatermark
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import coil3.compose.AsyncImage
import com.shopify.carto.feature.home.domain.model.Brand

@Composable
fun BrandCard(
    brand: Brand,
    compact: Boolean = true,
    onBrandClick: (Brand) -> Unit = {}
) {
    Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (compact) 8.dp else 12.dp).clickable { onBrandClick(brand) },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        if (brand.imageUrl.isNullOrBlank()) {

            Box(
                modifier = Modifier
                    .size(if (compact) 64.dp else 96.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.BrandingWatermark,
                    contentDescription = brand.name,
                    modifier = Modifier.size(if (compact) 28.dp else 40.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        } else {

            AsyncImage(
                model = brand.imageUrl,
                contentDescription = brand.name,
                modifier = Modifier
                    .size(if (compact) 64.dp else 96.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

        }

            Spacer(Modifier.height(10.dp))

            Text(
                text = brand.name,
                textAlign = TextAlign.Center,
                maxLines = 2,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }

}
