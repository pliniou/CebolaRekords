@file:Suppress("DEPRECATION")
package com.cebola.rekords.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Dark Color Scheme
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

// Light Color Scheme for accessibility
private val LightColorScheme = lightColorScheme(
    primary = BrandCyan,
    onPrimary = Color.White,
    primaryContainer = BrandCyan.copy(alpha = 0.1f),
    onPrimaryContainer = BrandCyan,
    secondary = BrandPurple,
    onSecondary = Color.White,
    secondaryContainer = BrandPurple.copy(alpha = 0.1f),
    onSecondaryContainer = BrandPurple,
    tertiary = BrandPink,
    onTertiary = Color.White,
    tertiaryContainer = BrandPink.copy(alpha = 0.1f),
    onTertiaryContainer = BrandPink,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color(0xFFF8F9FA),
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF1F3F4),
    onSurfaceVariant = Color(0xFF5F6368),
    surfaceContainer = Color(0xFFF8F9FA),
    surfaceContainerHigh = Color(0xFFE8EAED),
    surfaceContainerHighest = Color(0xFFE8EAED),
    surfaceContainerLow = Color.White,
    outline = Color(0xFF5F6368).copy(alpha = 0.5f),
    outlineVariant = Color(0xFF5F6368).copy(alpha = 0.3f),
    error = Color(0xFFD93025),
    onError = Color.White,
    errorContainer = Color(0xFFD93025).copy(alpha = 0.1f),
    onErrorContainer = Color(0xFFD93025),
    scrim = Color.Black.copy(alpha = 0.32f),
    inverseSurface = Color(0xFF303030),
    inverseOnSurface = Color.White,
    inversePrimary = BrandCyan,
)

@Composable
fun CebolaRekordsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val useDarkIcons = !darkTheme
            
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            
            WindowCompat.setDecorFitsSystemWindows(window, false)
            
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = useDarkIcons
            insetsController.isAppearanceLightNavigationBars = useDarkIcons
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}