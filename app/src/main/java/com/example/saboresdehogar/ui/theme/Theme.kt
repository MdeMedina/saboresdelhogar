package com.example.saboresdehogar.ui.theme

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Paleta de colores para el modo oscuro
// (Basada en tu archivo res/values/themes.xml)
private val DarkColorScheme = darkColorScheme(
    primary = ChileRedLight,
    onPrimary = Neutral900,
    primaryContainer = ChileRed,
    secondary = ChileBlueLight,
    onSecondary = Neutral900,
    background = Neutral900,
    surface = Neutral800,
    onBackground = Neutral100,
    onSurface = Neutral100
)

// Paleta de colores para el modo claro
// (Basada en tus diseños y colors.xml)
private val LightColorScheme = lightColorScheme(
    primary = ChileRed,
    onPrimary = ChileWhite,
    primaryContainer = ChileRedDark,
    secondary = ChileBlue,
    onSecondary = ChileWhite,
    background = AppBackground,
    surface = CardBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = DividerColor,
    outline = DividerColor
)

@Composable
fun SaboresDeHogarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color (Android 12+)
    dynamicColor: Boolean = false, // Lo desactivamos para usar nuestra marca
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
            // Usamos el color de fondo para la barra de estado
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Esto usa tu archivo Type.kt
        content = content
    )
}