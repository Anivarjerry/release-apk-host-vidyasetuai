package com.vidyasetuai.core.ui.theme

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
import com.vidyasetuai.core.ui.colors.AppColors

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.EmeraldGreen,
    onPrimary = Color.Black,
    secondary = AppColors.EmeraldGreen,
    onSecondary = Color.Black,
    background = AppColors.NearBlack,
    onBackground = Color.White,
    surface = AppColors.NearBlack,
    onSurface = Color.White,
    // Input field background: very dark surface
    surfaceVariant = Color(0xFF161616),
    onSurfaceVariant = Color(0xFFAAAAAA),
    // Idle input border
    outlineVariant = Color(0xFF2A2A2A)
)

private val LightColorScheme = lightColorScheme(
    primary = AppColors.EmeraldGreen,
    onPrimary = Color.White,
    secondary = AppColors.EmeraldGreen,
    onSecondary = Color.White,
    background = AppColors.PureWhite,
    onBackground = AppColors.CharcoalGray,
    surface = AppColors.PureWhite,
    onSurface = AppColors.CharcoalGray,
    // Input field background: very subtle gray tint
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF6B6B6B),
    // Idle input border
    outlineVariant = Color(0xFFE4E4E4)
)

@Composable
fun VidyaStuTheme(
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
            
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}