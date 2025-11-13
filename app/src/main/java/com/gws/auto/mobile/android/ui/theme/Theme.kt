package com.gws.auto.mobile.android.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    outline = md_theme_dark_outline,
    error = md_theme_dark_error,
    onError = md_theme_dark_onError
)

private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    outline = md_theme_light_outline,
    error = md_theme_light_error,
    onError = md_theme_light_onError
)

@Composable
fun GWSAutoForAndroidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    themeViewModel: ThemeViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val highlightColor by themeViewModel.highlightColor.collectAsState()

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> when (highlightColor) {
            "forest" -> DarkColorScheme.copy(primary = forest_theme_dark_primary, onPrimary = forest_theme_dark_onPrimary)
            "ocean" -> DarkColorScheme.copy(primary = ocean_theme_dark_primary, onPrimary = ocean_theme_dark_onPrimary)
            else -> DarkColorScheme
        }
        else -> when (highlightColor) {
            "forest" -> LightColorScheme.copy(primary = forest_theme_light_primary, onPrimary = forest_theme_light_onPrimary)
            "ocean" -> LightColorScheme.copy(primary = ocean_theme_light_primary, onPrimary = ocean_theme_light_onPrimary)
            else -> LightColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
