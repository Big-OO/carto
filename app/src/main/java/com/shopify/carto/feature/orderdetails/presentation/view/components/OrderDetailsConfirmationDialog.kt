package com.shopify.carto.feature.orderdetails.presentation.view.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.shopify.carto.R
import com.shopify.carto.feature.orderdetails.presentation.state.OrderDetailsDialog


@Composable
fun OrderDetailsConfirmationDialog(
    dialog: OrderDetailsDialog,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val title = when (dialog) {
        OrderDetailsDialog.CancelOrder -> stringResource(R.string.order_details_cancel_dialog_title)
        OrderDetailsDialog.HideOrder -> stringResource(R.string.order_details_remove_dialog_title)
    }
    val message = when (dialog) {
        OrderDetailsDialog.CancelOrder -> stringResource(R.string.order_details_cancel_dialog_message)
        OrderDetailsDialog.HideOrder -> stringResource(R.string.order_details_remove_dialog_message)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.order_details_confirm_negative_action),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.commonCancel))
            }
        },
    )
}
