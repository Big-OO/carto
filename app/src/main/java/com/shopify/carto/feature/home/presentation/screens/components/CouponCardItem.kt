package com.shopify.carto.feature.home.presentation.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shopify.carto.R
import com.shopify.carto.feature.home.domain.model.Coupon
import java.text.DecimalFormat

@Composable
fun CouponCardItem(
    coupon: Coupon,
    onCopyCodeClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    couponNumber: Int,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = couponGradientColors(couponNumber),
                    ),
                )
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = coupon.code,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = coupon.discountLabel(),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = { onCopyCodeClick(coupon.code) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                        contentPadding = PaddingValues(
                            horizontal = 16.dp,
                            vertical = 6.dp,
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.homeCouponCopyCode),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f),
                            CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = coupon.shortDiscountLabel(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun Coupon.discountLabel(): String {
    val value = discountValue.formatDiscountValue()
    return if (isPercentage) {
        stringResource(R.string.homeCouponPercentageOff, value)
    } else {
        stringResource(R.string.homeCouponFixedAmountOff, value)
    }
}

private fun Coupon.shortDiscountLabel(): String {
    val value = discountValue.formatDiscountValue()
    return if (isPercentage) "$value%" else value
}

private fun Double.formatDiscountValue(): String {
    return DecimalFormat("0.##").format(this)
}

@Composable
private fun couponGradientColors(couponNumber: Int): List<androidx.compose.ui.graphics.Color> {
    val colorScheme = MaterialTheme.colorScheme

    return when(couponNumber) {
        0 -> listOf(colorScheme.primary, colorScheme.error)
        1 -> listOf(colorScheme.tertiary, colorScheme.primary)
        else -> listOf(colorScheme.primary, colorScheme.outline)
    }
}