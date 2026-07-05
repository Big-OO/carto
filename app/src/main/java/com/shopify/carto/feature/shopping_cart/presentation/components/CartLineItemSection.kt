package com.shopify.carto.feature.shopping_cart.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.shopify.carto.feature.shopping_cart.domain.model.CartLine
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@Composable
fun CartLineItemSection(
    line: CartLine,
    currency: String,
    isUpdating: Boolean,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        AsyncImage(
            model = line.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = line.productTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = line.variantTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatPrice(line.price, currency),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (isUpdating) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    QuantityStepper(
                        quantity = line.quantity,
                        onIncrease = onIncreaseQuantity,
                        onDecrease = onDecreaseQuantity
                    )
                }
            }
        }
    }
}

@Composable
private fun QuantityStepper(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StepperIcon(icon = Icons.Filled.Remove, onClick = onDecrease)
        Text(text = quantity.toString(), fontWeight = FontWeight.SemiBold)
        StepperIcon(icon = Icons.Filled.Add, onClick = onIncrease)
    }
}

@Composable
private fun StepperIcon(icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(14.dp))
    }
}

private fun formatPrice(price: Double, currencyCode: String): String {
    return try {
        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
        format.currency = Currency.getInstance(currencyCode)
        format.format(price)
    } catch (exception: IllegalArgumentException) {
        "$currencyCode ${"%.2f".format(price)}"
    }
}