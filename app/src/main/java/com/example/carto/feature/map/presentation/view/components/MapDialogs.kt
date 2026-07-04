package com.example.carto.feature.map.presentation.view.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.carto.R
import com.example.carto.feature.map.presentation.model.MapActionDialog

@Composable
fun MapActionRequiredDialog(
    dialog: MapActionDialog,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val title = when (dialog) {
        MapActionDialog.LocationPermission -> R.string.map_location_permission_dialog_title
        MapActionDialog.GpsDisabled -> R.string.map_gps_dialog_title
    }
    val message = when (dialog) {
        MapActionDialog.LocationPermission -> R.string.map_location_permission_dialog_message
        MapActionDialog.GpsDisabled -> R.string.map_gps_dialog_message
    }
    val confirmText = when (dialog) {
        MapActionDialog.LocationPermission -> R.string.map_open_app_settings
        MapActionDialog.GpsDisabled -> R.string.map_open_location_settings
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.titleMedium,
            )
        },
        text = {
            Text(
                text = stringResource(message),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(confirmText))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.map_cancel))
            }
        }
    )
}

fun Context.openAppSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null),
    ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}

fun Context.openLocationSettings() {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}
