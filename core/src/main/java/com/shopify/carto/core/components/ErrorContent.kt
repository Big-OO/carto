package com.shopify.carto.core.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.shopify.carto.core.R
import com.shopify.carto.core.utils.AppError


data class ErrorUiContent(
    val icon: ImageVector,
    val title: String,
    val message: String,
    val tint: Color,
)

@Composable
fun rememberErrorUiContent(error: Throwable): ErrorUiContent {
    val errorColor = MaterialTheme.colorScheme.error

    return when (error) {

        is AppError.NoInternet -> ErrorUiContent(
            icon = Icons.Default.WifiOff,
            title = stringResource(R.string.errorNoInternetTitle),
            message = stringResource(R.string.errorNoInternetMessage),
            tint = errorColor,
        )

        is AppError.Timeout -> ErrorUiContent(
            icon = Icons.Default.CloudOff,
            title = stringResource(R.string.errorTimeoutTitle),
            message = stringResource(R.string.errorTimeoutMessage),
            tint = errorColor,
        )

        is AppError.Server -> {
            if (error.code == 404) {
                ErrorUiContent(
                    icon = Icons.Default.SearchOff,
                    title = stringResource(R.string.errorNotFoundTitle),
                    message = stringResource(R.string.errorNotFoundMessage),
                    tint = errorColor,
                )
            } else {
                ErrorUiContent(
                    icon = Icons.Default.ErrorOutline,
                    title = stringResource(R.string.errorServerTitle),
                    message = stringResource(R.string.errorServerMessage),
                    tint = errorColor,
                )
            }
        }

        else -> ErrorUiContent(
            icon = Icons.Default.ErrorOutline,
            title = stringResource(R.string.errorUnknownTitle),
            message = stringResource(R.string.errorUnknownMessage),
            tint = errorColor,
        )
    }
}
