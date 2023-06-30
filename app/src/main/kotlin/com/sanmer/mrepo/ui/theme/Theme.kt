package com.sanmer.mrepo.ui.theme

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect

@Composable
fun ComponentActivity.AppTheme(
    darkMode: Boolean,
    themeColor: Int,
    content: @Composable () -> Unit
) {
    val lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
    val darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)

    DisposableEffect(darkMode) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT,
            ) { darkMode },
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim,
                darkScrim,
            ) { darkMode }
        )
        onDispose {}
    }

    val color = Colors.getColor(id = themeColor)
    val colorScheme = when {
        darkMode -> color.darkColorScheme
        else -> color.lightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = Shapes,
        typography = Typography,
        content = content
    )
}