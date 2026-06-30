package com.example.carto.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ─────────────────────────────────────────────
//  Ocean Deep — Material3 Color Schemes
// ─────────────────────────────────────────────

private val LightColorScheme = lightColorScheme(
    primary              = Color.Black,
    onPrimary            = Color.White,
    primaryContainer     = Color(0xFFF5F5F5),
    onPrimaryContainer   = Color.Black,

    secondary            = Color.Black,
    onSecondary          = Color.White,
    secondaryContainer   = Color(0xFFF5F5F5),
    onSecondaryContainer = Color.Black,

    tertiary             = Color.Gray,
    onTertiary           = Color.White,
    tertiaryContainer    = Color(0xFFFAFAFA),
    onTertiaryContainer  = Color.Black,

    background           = Color.White,
    onBackground         = Color.Black,

    surface              = Color.White,
    onSurface            = Color.Black,
    surfaceVariant       = Color(0xFFF5F5F5),
    onSurfaceVariant     = Color.Gray,

    outline              = Color(0xFFD6D6D6),
    outlineVariant       = Color(0xFFE5E5E5),
)

private val DarkColorScheme = darkColorScheme(
    primary              = Color.White,
    onPrimary            = Color.Black,
    primaryContainer     = Color(0xFF1E1E1E),
    onPrimaryContainer   = Color.White,

    secondary            = Color.White,
    onSecondary          = Color.Black,
    secondaryContainer   = Color(0xFF1E1E1E),
    onSecondaryContainer = Color.White,

    tertiary             = Color.Gray,
    onTertiary           = Color.White,

    background           = Color.Black,
    onBackground         = Color.White,

    surface              = Color.Black,
    onSurface            = Color.White,
    surfaceVariant       = Color(0xFF121212),
    onSurfaceVariant     = Color.LightGray,

    outline              = Color(0xFF2C2C2C),
    outlineVariant       = Color(0xFF1E1E1E),
)

@Composable
fun CartoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color disabled — use our Ocean Deep palette
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if (darkTheme) Color.Black.toArgb() else Color.White.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}