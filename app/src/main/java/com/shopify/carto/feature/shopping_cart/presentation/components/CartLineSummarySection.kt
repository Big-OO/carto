package com.shopify.carto.feature.shopping_cart.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shopify.carto.R
import com.shopify.carto.feature.shopping_cart.presentation.CartEvent

@Composable
fun CartSummarySection(
    subtotal: Double,
    total: Double,
    currency: String,
    onCheckoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SummaryRow(label = stringResource(id = R.string.cartSubTotal), value = subtotal, currency = currency)

        HorizontalDivider()

        SummaryRow(label = stringResource(id = R.string.cartTotal), value = total, currency = currency, emphasize = true)

        Button(
            onClick = onCheckoutClick,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
        ) {
            Text(text = stringResource(id = R.string.cartGoToCheckout), fontWeight = FontWeight.SemiBold)
        }
    }
}


@Composable
private fun SummaryRow(
    label: String,
    value: Double,
    currency: String,
    emphasize: Boolean = false
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = label,
            style = if (emphasize) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Normal,
            color = if (emphasize) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
        val formatter = com.shopify.carto.feature.currency.presentation.format.LocalCurrencyFormatter.current
        Text(
            text = formatter.format(value),
            style = if (emphasize) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Normal
        )
    }
}