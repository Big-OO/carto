package com.shopify.carto.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.core.view.WindowCompat
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import com.shopify.carto.feature.settings.domain.model.AppTheme

class CartoColors(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val outlineVariant: Color,
    val error: Color,
    val onError: Color,
)

class CartoTypography(
    val displayLarge: TextStyle,
    val headlineLarge: TextStyle,
    val headlineMedium: TextStyle,
    val headlineSmall: TextStyle,
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val titleSmall: TextStyle,
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val bodySmall: TextStyle,
    val labelLarge: TextStyle,
    val labelMedium: TextStyle,
    val labelSmall: TextStyle,
)

val LocalCartoColors = staticCompositionLocalOf<CartoColors> {
    error("No CartoColors provided")
}

val LocalCartoTypography = staticCompositionLocalOf<CartoTypography> {
    error("No CartoTypography provided")
}

object CartoTheme {
    val colors: CartoColors
        @Composable
        @ReadOnlyComposable
        get() = LocalCartoColors.current

    val typography: CartoTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalCartoTypography.current
}

private val LightColorScheme = CartoColors(
    primary = CartoBlack,
    onPrimary = CartoWhite,
    primaryContainer = CartoSurfaceSoft,
    onPrimaryContainer = CartoTextPrimary,
    secondary = CartoTextSecondary,
    tertiary = CartoSuccess,
    onTertiary = CartoWhite,
    onSecondary = CartoWhite,
    secondaryContainer = CartoSurfaceSoft,
    onSecondaryContainer = CartoTextPrimary,
    background = CartoBackground,
    onBackground = CartoTextPrimary,
    surface = CartoSurface,
    onSurface = CartoTextPrimary,
    surfaceVariant = CartoSurfaceSoft,
    onSurfaceVariant = CartoTextSecondary,
    outline = CartoOutline,
    outlineVariant = CartoOutlineStrong,
    error = CartoError,
    onError = CartoWhite,
)

private val DarkColorScheme = CartoColors(
    primary = CartoWhite,
    onPrimary = CartoBlack,
    primaryContainer = CartoDarkSurfaceSoft,
    onPrimaryContainer = CartoDarkTextPrimary,
    secondary = CartoDarkTextSecondary,
    tertiary = CartoSuccess,
    onTertiary = CartoWhite,
    onSecondary = CartoBlack,
    secondaryContainer = CartoDarkSurfaceSoft,
    onSecondaryContainer = CartoDarkTextPrimary,
    background = CartoDarkBackground,
    onBackground = CartoDarkTextPrimary,
    surface = CartoDarkSurface,
    onSurface = CartoDarkTextPrimary,
    surfaceVariant = CartoDarkSurfaceSoft,
    onSurfaceVariant = CartoDarkTextSecondary,
    outline = CartoDarkOutline,
    outlineVariant = CartoDarkOutline,
    error = CartoError,
    onError = CartoWhite,
)

private val EyeColorScheme = CartoColors(
    primary = CartoBlack,
    onPrimary = CartoWhite,
    primaryContainer = Color(0xFFF6F0DD),
    onPrimaryContainer = CartoTextPrimary,
    secondary = CartoTextSecondary,
    tertiary = CartoSuccess,
    onTertiary = CartoWhite,
    onSecondary = CartoWhite,
    secondaryContainer = Color(0xFFF6F0DD),
    onSecondaryContainer = CartoTextPrimary,
    background = Color(0xFFFAF4E0),
    onBackground = CartoTextPrimary,
    surface = Color(0xFFFFFDF5),
    onSurface = CartoTextPrimary,
    surfaceVariant = Color(0xFFF1EAD2),
    onSurfaceVariant = CartoTextSecondary,
    outline = Color(0xFFE4DCBF),
    outlineVariant = Color(0xFFD3CAA8),
    error = CartoError,
    onError = CartoWhite,
)

@Composable
fun CartoTheme(
    appTheme: AppTheme = AppTheme.LIGHT,
    content: @Composable () -> Unit,
) {
    val colorScheme = when (appTheme) {
        AppTheme.LIGHT -> LightColorScheme
        AppTheme.EYE_MODE -> EyeColorScheme
        AppTheme.DARK -> DarkColorScheme
    }
    val darkTheme = appTheme == AppTheme.DARK
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            var context = view.context
            var activity: Activity? = null
            while (context is android.content.ContextWrapper) {
                if (context is Activity) {
                    activity = context
                    break
                }
                context = context.baseContext
            }
            activity?.window?.let { window ->
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    val materialColorScheme = if (darkTheme) {
        darkColorScheme(
            primary = colorScheme.primary,
            onPrimary = colorScheme.onPrimary,
            primaryContainer = colorScheme.primaryContainer,
            onPrimaryContainer = colorScheme.onPrimaryContainer,
            secondary = colorScheme.secondary,
            onSecondary = colorScheme.onSecondary,
            background = colorScheme.background,
            onBackground = colorScheme.onBackground,
            surface = colorScheme.surface,
            onSurface = colorScheme.onSurface,
            surfaceVariant = colorScheme.surfaceVariant,
            onSurfaceVariant = colorScheme.onSurfaceVariant,
            outline = colorScheme.outline,
            error = colorScheme.error,
            onError = colorScheme.onError
        )
    } else {
        lightColorScheme(
            primary = colorScheme.primary,
            onPrimary = colorScheme.onPrimary,
            primaryContainer = colorScheme.primaryContainer,
            onPrimaryContainer = colorScheme.onPrimaryContainer,
            secondary = colorScheme.secondary,
            onSecondary = colorScheme.onSecondary,
            background = colorScheme.background,
            onBackground = colorScheme.onBackground,
            surface = colorScheme.surface,
            onSurface = colorScheme.onSurface,
            surfaceVariant = colorScheme.surfaceVariant,
            onSurfaceVariant = colorScheme.onSurfaceVariant,
            outline = colorScheme.outline,
            error = colorScheme.error,
            onError = colorScheme.onError
        )
    }

    MaterialTheme(
        colorScheme = materialColorScheme
    ) {
        CompositionLocalProvider(
            LocalCartoColors provides colorScheme,
            LocalCartoTypography provides CartoTypographySetup,
            content = content
        )
    }
}