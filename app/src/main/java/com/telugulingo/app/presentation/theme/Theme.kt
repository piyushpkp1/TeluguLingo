package com.telugulingo.app.presentation.theme

import android.app.Activity
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

private val DarkColorScheme = darkColorScheme(
    primary = BrandPrimaryDarkMode,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = BrandSecondaryDarkMode,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF7C2D12),
    onSecondaryContainer = Color(0xFFFFEDD5),
    tertiary = BrandTertiary,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceDarkElevated,
    onSurfaceVariant = OnSurfaceDarkMuted,
    error = ErrorRed,
    onError = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = BrandPrimarySoft,
    onPrimaryContainer = BrandPrimaryDark,
    secondary = BrandSecondary,
    onSecondary = Color.White,
    secondaryContainer = BrandSecondarySoft,
    onSecondaryContainer = Color(0xFF9A3412),
    tertiary = BrandTertiary,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLightElevated,
    onSurface = OnSurfaceLight,
    surfaceVariant = Color(0xFFF4F4F5),
    onSurfaceVariant = OnSurfaceLightMuted,
    error = ErrorRed,
    onError = Color.White,
)

@Composable
fun TeluguLingoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
