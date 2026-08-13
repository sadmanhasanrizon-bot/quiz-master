package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = NaturalPrimaryDark,
    onPrimary = NaturalOnPrimaryDark,
    secondary = NaturalSecondaryDark,
    onSecondary = NaturalOnSecondaryDark,
    tertiary = NaturalAccentGoldDark,
    background = NaturalBackgroundDark,
    onBackground = NaturalTextPrimaryDark,
    surface = NaturalSurfaceDark,
    onSurface = NaturalTextPrimaryDark,
    surfaceVariant = NaturalSurfaceVariantDark,
    onSurfaceVariant = NaturalTextSecondaryDark,
    outline = NaturalBorderDark,
    error = NaturalError
)

private val LightColorScheme = lightColorScheme(
    primary = NaturalPrimaryLight,
    onPrimary = NaturalOnPrimaryLight,
    secondary = NaturalSecondaryLight,
    onSecondary = NaturalOnSecondaryLight,
    tertiary = NaturalAccentGold,
    background = NaturalBackgroundLight,
    onBackground = NaturalTextPrimaryLight,
    surface = NaturalSurfaceLight,
    onSurface = NaturalTextPrimaryLight,
    surfaceVariant = NaturalSurfaceVariantLight,
    onSurfaceVariant = NaturalTextSecondaryLight,
    outline = NaturalBorderLight,
    error = NaturalError
)

@Composable
fun MyApplicationTheme(
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
