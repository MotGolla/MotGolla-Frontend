package com.motgolla.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme

private val LightColors = lightColorScheme(
    primary = MotgollaPrimary,
    onPrimary = White,
    secondary = MotgollaSecondary,
    onSecondary = White,
    background = White,
    onBackground = Black,
    surface = White,
    onSurface = Black
)

private val DarkColors = darkColorScheme(
    primary = MotgollaPrimary,
    onPrimary = Black,
    secondary = MotgollaSecondary,
    onSecondary = Black,
    background = Black,
    onBackground = White,
    surface = Black,
    onSurface = White
)

@Composable
fun MotgollaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}