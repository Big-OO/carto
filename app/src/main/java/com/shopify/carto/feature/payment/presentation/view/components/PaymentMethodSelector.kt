package com.shopify.carto.feature.payment.presentation.view.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shopify.carto.feature.payment.domain.model.PaymentMethod

@Composable
fun PaymentMethodSelector(
    selectedMethod: PaymentMethod,
    onMethodSelected: (PaymentMethod) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Payment Method",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        PaymentMethodCard(
            icon = { modifier ->
                Icon(
                    imageVector = Icons.Outlined.CreditCard,
                    contentDescription = null,
                    modifier = modifier,
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            title = "Pay with Card",
            subtitle = "Visa, Mastercard via Paymob",
            isSelected = selectedMethod == PaymentMethod.CARD,
            onClick = { onMethodSelected(PaymentMethod.CARD) },
        )

        PaymentMethodCard(
            icon = { modifier ->
                Icon(
                    imageVector = Icons.Outlined.AccountBalanceWallet,
                    contentDescription = null,
                    modifier = modifier,
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            title = "Digital Wallet",
            subtitle = "Vodafone Cash, Orange Money, etc.",
            isSelected = selectedMethod == PaymentMethod.DIGITAL_WALLET,
            onClick = { onMethodSelected(PaymentMethod.DIGITAL_WALLET) },
        )

        PaymentMethodCard(
            icon = { modifier ->
                Icon(
                    imageVector = Icons.Outlined.LocalShipping,
                    contentDescription = null,
                    modifier = modifier,
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            title = "Cash on Delivery",
            subtitle = "Pay when your order arrives",
            isSelected = selectedMethod == PaymentMethod.CASH_ON_DELIVERY,
            onClick = { onMethodSelected(PaymentMethod.CASH_ON_DELIVERY) },
        )
    }
}

@Composable
private fun PaymentMethodCard(
    icon: @Composable (Modifier) -> Unit,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outline
        },
        animationSpec = tween(durationMillis = 200),
        label = "borderColor",
    )

    val containerColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(durationMillis = 200),
        label = "containerColor",
    )

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = borderColor,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon(Modifier.size(28.dp))

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = MaterialTheme.colorScheme.outline,
                ),
            )
        }
    }
}
