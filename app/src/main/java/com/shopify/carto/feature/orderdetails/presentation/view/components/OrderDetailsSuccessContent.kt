package com.shopify.carto.feature.orderdetails.presentation.view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shopify.carto.R
import com.shopify.carto.feature.orderdetails.presentation.model.OrderDetailsUi

@Composable
fun OrderDetailsSuccessContent(
    order: OrderDetailsUi,
    isProcessingAction: Boolean,
    onCancelOrderClick: () -> Unit,
    onHideOrderClick: () -> Unit,
) {
    val scrollState = rememberScrollState()

    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 5 }),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            OrderHeroCard(order)

            OrderSectionCard(
                title = stringResource(R.string.order_details_products_title),
                icon = Icons.Outlined.ShoppingBag,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    order.items.forEach { item ->
                        OrderProductRow(item)
                    }
                }
            }

            OrderSectionCard(
                title = stringResource(R.string.order_details_payment_summary_title),
                icon = Icons.Outlined.Payments,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SummaryRow(stringResource(R.string.order_details_subtotal), order.subtotal)
                    SummaryRow(stringResource(R.string.order_details_discount), order.discount)
                    SummaryRow(stringResource(R.string.order_details_shipping), order.shipping)
                    SummaryRow(stringResource(R.string.order_details_tax), order.tax)
                    SummaryRow(
                        label = stringResource(R.string.order_details_total),
                        value = order.total,
                        emphasized = true,
                    )
                }
            }

            OrderSectionCard(
                title = stringResource(R.string.order_details_extra_info_title),
                icon = Icons.AutoMirrored.Outlined.ReceiptLong,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SummaryRow(stringResource(R.string.order_details_payment_method), order.paymentMethods)
                    SummaryRow(stringResource(R.string.order_details_coupons), order.discountCodes)
                    SummaryRow(stringResource(R.string.order_details_confirmation_number), order.confirmationNumber)
                    SummaryRow(stringResource(R.string.order_details_lifecycle), order.lifecycleStatus)
                }
            }

            OrderSectionCard(
                title = stringResource(R.string.order_details_shipping_title),
                icon = Icons.Outlined.LocalShipping,
            ) {
                Text(
                    text = order.shippingAddress,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            OrderActionButton(
                order = order,
                isProcessingAction = isProcessingAction,
                onCancelOrderClick = onCancelOrderClick,
                onHideOrderClick = onHideOrderClick,
            )
        }
    }
}
