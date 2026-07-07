package com.shopify.carto.feature.product_details.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@Composable
fun ProductDetailsPriceSection(
    price: Double,
    currency: String,
    isInCart: Boolean,
    isOutOfStock: Boolean,
    isAddingToCart: Boolean,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.productDetailsPrice),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val formatter = com.shopify.carto.feature.currency.presentation.format.LocalCurrencyFormatter.current
            Text(
                text = formatter.format(price),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onButtonClick,
            enabled = !isOutOfStock && !isAddingToCart,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(
                horizontal = 24.dp,
                vertical = 14.dp
            )
        ) {
            if (isAddingToCart) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Icon(
                    imageVector = if (isInCart) Icons.Filled.Check else Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = when {
                    isOutOfStock -> stringResource(id = R.string.productDetailsOutOfStock)
                    isInCart -> stringResource(id = R.string.productDetailsInCart)
                    else -> stringResource(id = R.string.productDetailsAddToCart)
                },
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

