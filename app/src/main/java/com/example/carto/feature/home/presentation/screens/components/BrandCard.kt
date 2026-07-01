package com.example.carto.feature.home.presentation.screens.components

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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import coil3.compose.AsyncImage
import com.example.carto.feature.home.domain.model.Brand

@Composable
fun BrandCard(
    brand: Brand,
    compact: Boolean = true
) {

    Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (compact) 8.dp else 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        if (brand.imageUrl == null) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = brand.name,
                modifier = Modifier.size(if (compact) 64.dp else 96.dp)
            )
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
