@file:Suppress("DEPRECATION")

package com.cebolarekords.player.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = BrandCyan,
    onPrimary = OnPrimary,
    primaryContainer = BrandCyan.copy(alpha = 0.2f),
    onPrimaryContainer = BrandCyan,
    secondary = BrandPurple,
    onSecondary = OnPrimary,
    secondaryContainer = BrandPurple.copy(alpha = 0.2f),
    onSecondaryContainer = BrandPurple,
    tertiary = BrandPink,
    onTertiary = OnPrimary,
    tertiaryContainer = BrandPink.copy(alpha = 0.2f),
    onTertiaryContainer = BrandPink,
    background = BackgroundDark,
    onBackground = OnSurface,
    surface = SurfaceDark,
    onSurface = OnSurface,
    surfaceVariant = SurfaceContainerHigh,
    onSurfaceVariant = OnSurfaceVariant,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHigh.copy(alpha = 0.8f),
    surfaceContainerLow = SurfaceDark,
    outline = OnSurfaceVariant.copy(alpha = 0.5f),
    outlineVariant = OnSurfaceVariant.copy(alpha = 0.3f),
    error = Error,
    onError = OnPrimary,
    errorContainer = Error.copy(alpha = 0.2f),
    onErrorContainer = Error,
    scrim = Color.Black.copy(alpha = 0.6f),
    inverseSurface = OnSurface,
    inverseOnSurface = BackgroundDark,
    inversePrimary = BrandCyan,
)

@Composable
fun CebolaRekordsTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> DarkColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val useDarkIcons = !darkTheme
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false) // Edge-to-edge
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = useDarkIcons
                isAppearanceLightNavigationBars = useDarkIcons
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}