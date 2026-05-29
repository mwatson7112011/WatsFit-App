package com.homepantry.ui.theme

import androidx.compose.ui.graphics.Color

val NavyPrimary = Color(0xFF0D1B2A)
val NavyDark = Color(0xFF08121C)
val OliveGreen = Color(0xFF6B8F4E)
val SkyBlue = Color(0xFF2A7B9B)
val LightGrey = Color(0xFFE0E0E0)
val DarkGrey = Color(0xFF1B263B)
val AlertCoral = Color(0xFFE76F51)
val WarningGold = Color(0xFFE9C46A)

val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = OliveGreen,
    onPrimary = Color.White,
    primaryContainer = NavyPrimary,
    onPrimaryContainer = Color.White,
    secondary = SkyBlue,
    onSecondary = Color.White,
    background = NavyDark,
    onBackground = Color.White,
    surface = DarkGrey,
    onSurface = Color.White,
    error = AlertCoral
)

val LightColorScheme = androidx.compose.material3.lightColorScheme(
    primary = OliveGreen,
    onPrimary = Color.White,
    secondary = SkyBlue,
    onSecondary = Color.White,
    background = Color.White,
    onBackground = NavyDark,
    surface = LightGrey,
    onSurface = NavyDark
)
