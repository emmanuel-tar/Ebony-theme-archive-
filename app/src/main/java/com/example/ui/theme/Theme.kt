package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = SleekPrimary,
    secondary = SleekPrimaryContainer,
    tertiary = SleekBadgePink,
    background = Color(0xFF141218), // Sleek Dark Base
    surface = Color(0xFF1D1B20), // Sleek Dark Surface
    onPrimary = SleekOnPrimary,
    onSecondary = SleekOnPrimaryContainer,
    onTertiary = SleekBadgePinkText,
    onBackground = Color(0xFFE6E1E5),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = SleekPrimary,
    secondary = SleekPrimaryContainer,
    tertiary = SleekBadgePink,
    background = SleekBackground,
    surface = SleekSurface,
    onPrimary = SleekOnPrimary,
    onSecondary = SleekOnPrimaryContainer,
    onTertiary = SleekBadgePinkText,
    onBackground = SleekTextDark,
    onSurface = SleekTextDark,
    surfaceVariant = Color(0xFFE6E1E5),
    onSurfaceVariant = SleekTextMuted
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false, // Sleek Interface is light-themed by default
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
