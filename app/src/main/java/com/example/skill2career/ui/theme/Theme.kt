package com.example.skill2career.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary               = NavyDeep,
    onPrimary             = Color.White,
    primaryContainer      = NavyLight,
    onPrimaryContainer    = Ivory,
    secondary             = Gold,
    onSecondary           = NavyDeep,
    secondaryContainer    = GoldLight,
    onSecondaryContainer  = NavyDeep,
    tertiary              = ForestGreen,
    onTertiary            = Color.White,
    tertiaryContainer     = ForestGreenBg,
    onTertiaryContainer   = ForestGreen,
    error                 = Burgundy,
    onError               = Color.White,
    errorContainer        = BurgundyBg,
    onErrorContainer      = Burgundy,
    background            = Ivory,
    onBackground          = TextPrimary,
    surface               = CardSurface,
    onSurface             = TextPrimary,
    surfaceVariant        = GoldLight,
    onSurfaceVariant      = TextSecondary,
    outline               = DividerLight,
    outlineVariant        = DividerLight
)

private val DarkColorScheme = darkColorScheme(
    primary               = Gold,
    onPrimary             = NavyDeep,
    primaryContainer      = NavyMid,
    onPrimaryContainer    = Gold,
    secondary             = GoldLight,
    onSecondary           = NavyDeep,
    secondaryContainer    = NavyMid,
    onSecondaryContainer  = GoldLight,
    tertiary              = ForestGreen,
    onTertiary            = Color.White,
    error                 = Burgundy,
    onError               = Color.White,
    background            = NavyDark,
    onBackground          = Ivory,
    surface               = SurfaceDark,
    onSurface             = Ivory,
    surfaceVariant        = NavyMid,
    onSurfaceVariant      = TextMuted,
    outline               = NavyLight
)

@Composable
fun SKILL2careerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = NavyDeep.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
