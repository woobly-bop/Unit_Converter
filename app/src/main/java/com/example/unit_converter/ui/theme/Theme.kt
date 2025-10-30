package com.example.unit_converter.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = BluePrimary,
    secondary = BlueSecondary,
    background = BlueBackground,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onBackground = BluePrimary,
    onSurface = BluePrimary
)

@Composable
fun UnitConverterTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}