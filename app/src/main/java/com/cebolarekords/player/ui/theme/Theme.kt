@file:Suppress("DEPRECATION")

package com.cebolarekords.player.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = PurpleDeep,
    onPrimary = TextPrimary,
    primaryContainer = PurpleLight,
    onPrimaryContainer = TextPrimary,

    secondary = AccentCyan,
    onSecondary = TextPrimary,
    secondaryContainer = AccentCyan.copy(alpha = 0.2f),
    onSecondaryContainer = AccentCyan,

    tertiary = AccentPink,
    onTertiary = TextPrimary,
    tertiaryContainer = AccentPink.copy(alpha = 0.2f),
    onTertiaryContainer = AccentPink,

    background = BackgroundDark,
    onBackground = TextPrimary,

    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextTertiary,

    surfaceContainer = SurfaceLight,
    surfaceContainerHigh = SurfaceVariant,
    surfaceContainerHighest = Color(0xFF404040),

    outline = TextMuted,
    outlineVariant = Color(0xFF525252),

    error = Error,
    onError = TextPrimary,
    errorContainer = Error.copy(alpha = 0.2f),
    onErrorContainer = Error,

    inverseSurface = TextPrimary,
    inverseOnSurface = BackgroundDark,
    inversePrimary = PurpleLight,

    scrim = Color.Black.copy(alpha = 0.5f)
)

private val LightColorScheme = lightColorScheme(
    primary = PurpleDeep,
    onPrimary = Color.White,
    primaryContainer = PurpleLight.copy(alpha = 0.1f),
    onPrimaryContainer = PurpleDeep,

    secondary = AccentCyan,
    onSecondary = Color.White,
    secondaryContainer = AccentCyan.copy(alpha = 0.1f),
    onSecondaryContainer = AccentCyan,

    tertiary = AccentPink,
    onTertiary = Color.White,
    tertiaryContainer = AccentPink.copy(alpha = 0.1f),
    onTertiaryContainer = AccentPink,

    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),

    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),

    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),

    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = PurpleLight
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
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}