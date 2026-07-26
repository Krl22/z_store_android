package com.zeta.store.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BosqueLime,
    onPrimary = BosqueGreenDeep,
    secondary = BosqueSurfaceStrong,
    tertiary = BosqueWarmth,
    background = BosqueInk,
    surface = BosqueGreenDeep,
    onBackground = BosqueSurface,
    onSurface = BosqueSurface,
)

private val LightColorScheme = lightColorScheme(
    primary = BosqueGreenDeep,
    onPrimary = Color.White,
    secondary = BosqueGreen,
    onSecondary = Color.White,
    tertiary = BosqueWarmth,
    background = BosqueBackground,
    onBackground = BosqueInk,
    surface = BosqueSurface,
    onSurface = BosqueInk,
    surfaceVariant = BosqueSurfaceStrong,
    onSurfaceVariant = BosqueMuted,
    outline = BosqueBorder,
)

@Composable
fun ZetaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
